param([switch]$SkipVerify, [switch]$DryRun, [string]$Message = "")
$ErrorActionPreference = "Continue"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ConfigPath = Join-Path $ScriptDir "deploy.config.json"
$Server = "47.77.202.75"; $User = "root"
$SSHKey = "$env:USERPROFILE\.ssh\ali_overseas_openssh"
$SSHPort = 22; $RemoteDir = "/var/www/posturemind"
$NginxReload = $true; $Domain = "manus.xin"
$Files = @("index.html","styles.css","app.js","knowledge.js","analyzer.js","exercises-ui.js","manifest.json")
if (Test-Path $ConfigPath) {
    try {
        $cfg = Get-Content $ConfigPath -Raw | ConvertFrom-Json
        if ($cfg.server) { $Server = $cfg.server }
        if ($cfg.user) { $User = $cfg.user }
        if ($cfg.sshKey) { $SSHKey = $cfg.sshKey }
        if ($cfg.sshPort) { $SSHPort = $cfg.sshPort }
        if ($cfg.remoteDir) { $RemoteDir = $cfg.remoteDir }
        if ($cfg.nginxReload -ne $null) { $NginxReload = $cfg.nginxReload }
        if ($cfg.domain) { $Domain = $cfg.domain }
        if ($cfg.files) { $Files = $cfg.files }
    } catch { Write-Host "[WARN] config parse failed, using defaults" -ForegroundColor Yellow }
}
$Remote = "$User@$Server"
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$BackupName = "posturemind-backup-$Timestamp"
$RemoteTmp = "/tmp/posturemind-staging-$Timestamp"

function Run-Remote($cmd) {
    if ($DryRun) { Write-Host "  [DRY-RUN] ssh $Remote $cmd" -ForegroundColor Gray; return "DRY_RUN_OK" }
    $out = ssh -i $SSHKey -p $SSHPort -o BatchMode=yes $Remote $cmd 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) { throw "SSH failed (exit $LASTEXITCODE): $cmd`n$out" }
    return $out
}

Write-Host ""
Write-Host ">> Preflight" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
$missing = @()
foreach ($f in $Files) { if (-not (Test-Path (Join-Path $ScriptDir $f))) { $missing += $f } }
if ($missing.Count -gt 0) { Write-Host "[FAIL] Missing: $($missing -join ', ')" -ForegroundColor Red; exit 1 }
Write-Host "[OK] $($Files.Count) files present" -ForegroundColor Green
if (-not (Test-Path $SSHKey)) { Write-Host "[FAIL] SSH key not found: $SSHKey" -ForegroundColor Red; exit 1 }
Write-Host "[OK] SSH key ready" -ForegroundColor Green
$test = Run-Remote "echo connected"
if ($DryRun) { Write-Host "[SKIP] Dry-run mode, skipping real connection test" -ForegroundColor Gray }
elseif ($test -match "connected") { Write-Host "[OK] Server reachable ($Server)" -ForegroundColor Green }
else { Write-Host "[FAIL] Cannot reach server" -ForegroundColor Red; exit 1 }
$nginxStatus = (Run-Remote "systemctl is-active nginx").Trim()
if ($nginxStatus -eq "active") { Write-Host "[OK] nginx active" -ForegroundColor Green }
else { Write-Host "[WARN] nginx: $nginxStatus" -ForegroundColor Yellow }

Write-Host ""
Write-Host ">> Pack local files" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
$StagingDir = Join-Path $env:TEMP "posturemind-deploy-$Timestamp"
if (Test-Path $StagingDir) { Remove-Item $StagingDir -Recurse -Force }
New-Item -ItemType Directory -Path $StagingDir -Force | Out-Null
foreach ($f in $Files) {
    if ($DryRun) { Write-Host "  [DRY-RUN] Copy $f" -ForegroundColor Gray }
    else { Copy-Item (Join-Path $ScriptDir $f) $StagingDir -Force }
}
$totalSize = (Get-ChildItem $StagingDir -Recurse | Measure-Object Length -Sum).Sum
Write-Host "[OK] Packed: $($Files.Count) files, $([math]::Round($totalSize/1024, 1)) KB" -ForegroundColor Green

Write-Host ""
Write-Host ">> Backup current version" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
$backupCmd = "if [ -d $RemoteDir ]; then cp -r $RemoteDir /tmp/$BackupName; echo OK; else echo NONE; fi"
$backupResult = Run-Remote $backupCmd
if ($backupResult -match "OK") { Write-Host "[OK] Backed up to /tmp/$BackupName" -ForegroundColor Green }
else { Write-Host "[INFO] First deploy, nothing to backup" -ForegroundColor Gray }

Write-Host ""
Write-Host ">> Upload to server" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
Run-Remote "mkdir -p $RemoteTmp" | Out-Null
foreach ($f in $Files) {
    $localFile = Join-Path $StagingDir $f
    if ($DryRun) { Write-Host "  [DRY-RUN] scp $f" -ForegroundColor Gray }
    else {
        & scp -i $SSHKey -P $SSHPort $localFile "${Remote}:${RemoteTmp}/${f}" 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) { throw "scp failed: $f" }
    }
}
Write-Host "[OK] Upload done" -ForegroundColor Green

Write-Host ""
Write-Host ">> Deploy to production" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
$deployCmd = "mkdir -p $RemoteDir; cp -f $RemoteTmp/* $RemoteDir/; chmod 644 $RemoteDir/*; chown -R nginx:nginx $RemoteDir 2>/dev/null; ls -la $RemoteDir/; rm -rf $RemoteTmp"
Run-Remote $deployCmd | Out-Null
Write-Host "[OK] Deployed to $RemoteDir" -ForegroundColor Green

if ($NginxReload) {
    Write-Host ""
    Write-Host ">> Reload nginx" -ForegroundColor Cyan
    Write-Host "------------------------------------------------------------"
    Run-Remote "nginx -t" | Out-Null
    Run-Remote "systemctl reload nginx" | Out-Null
    Write-Host "[OK] nginx reloaded" -ForegroundColor Green
}

if (-not $SkipVerify) {
    Write-Host ""
    Write-Host ">> Verify" -ForegroundColor Cyan
    Write-Host "------------------------------------------------------------"
    $baseUrl = $Domain -replace "^https?://", ""
    $httpsUrl = "https://$baseUrl"
    $httpStatus = (Run-Remote "curl -sk -o /dev/null -w '%{http_code}' $httpsUrl/").Trim()
    if ($httpStatus -eq "200") { Write-Host "[OK] $httpsUrl/  ->  HTTP $httpStatus" -ForegroundColor Green }
    else { Write-Host "[WARN] $httpsUrl/  ->  HTTP $httpStatus" -ForegroundColor Yellow }
    if (-not $DryRun) {
        $remoteSize = (Run-Remote "curl -sk $httpsUrl/ | wc -c").Trim()
        $localSize = (Get-Item (Join-Path $StagingDir "index.html")).Length
        Write-Host "    Remote: $remoteSize bytes | Local: $localSize bytes"
        if ([int]$remoteSize -eq $localSize) { Write-Host "[OK] Sizes match" -ForegroundColor Green }
        else { Write-Host "[WARN] Size mismatch (maybe cache)" -ForegroundColor Yellow }
    } else {
        Write-Host "    [DRY-RUN] Skipping size compare" -ForegroundColor Gray
    }
    foreach ($f in @("app.js","knowledge.js","styles.css","analyzer.js","exercises-ui.js","manifest.json")) {
        $status = (Run-Remote "curl -sk -o /dev/null -w '%{http_code}' $httpsUrl/$f").Trim()
        if ($status -eq "200") { Write-Host "[OK] $f -> $status" -ForegroundColor Green }
        else { Write-Host "[FAIL] $f -> $status" -ForegroundColor Red }
    }
}

Write-Host ""
Write-Host ">> Cleanup" -ForegroundColor Cyan
Write-Host "------------------------------------------------------------"
if (-not $DryRun) { Remove-Item $StagingDir -Recurse -Force }
Write-Host "[OK] Staging cleaned" -ForegroundColor Green

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host " Deploy complete" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
$baseUrl = $Domain -replace "^https?://", ""
Write-Host "  https://$baseUrl/" -ForegroundColor Cyan
if ($Message) { Write-Host "  $Message" -ForegroundColor Gray }
Write-Host ""
Write-Host "Backup: /tmp/$BackupName" -ForegroundColor Gray
Write-Host ""
Write-Host "Rollback:" -ForegroundColor Gray
$rb1 = "ssh " + $Remote
$rb2 = " 'rm -rf " + $RemoteDir + " ; mv /tmp/" + $BackupName + " " + $RemoteDir + "'"
Write-Host ("  " + $rb1 + $rb2) -ForegroundColor Gray
Write-Host ""
