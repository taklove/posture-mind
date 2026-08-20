#!/bin/bash
# ============================================================
# 正形 PostureMind — 一键部署脚本 (Linux/macOS)
#
# 用法:
#   ./deploy.sh                    # 部署 + 验证
#   ./deploy.sh --skip-verify      # 部署但跳过验证
#   ./deploy.sh --dry-run          # 只看看要做什么
#   ./deploy.sh --message "fix"    # 带说明
# ============================================================

set -euo pipefail

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$SCRIPT_DIR"
CONFIG_FILE="$SCRIPT_DIR/deploy.config.json"

# 默认配置
SERVER="47.77.202.75"
USER="root"
SSH_KEY="$HOME/.ssh/ali_overseas_openssh"
SSH_PORT=22
REMOTE_DIR="/var/www/posturemind"
NGINX_RELOAD=true
DOMAIN="https://manus.xin"
FILES=("index.html" "styles.css" "app.js" "knowledge.js" "analyzer.js" "exercises-ui.js" "manifest.json")
MESSAGE=""

# 读取配置覆盖
if [ -f "$CONFIG_FILE" ] && command -v jq >/dev/null 2>&1; then
    SERVER=$(jq -r '.server // empty' "$CONFIG_FILE")
    USER=$(jq -r '.user // empty' "$CONFIG_FILE")
    SSH_KEY=$(jq -r '.sshKey // empty' "$CONFIG_FILE")
    SSH_PORT=$(jq -r '.sshPort // empty' "$CONFIG_FILE")
    REMOTE_DIR=$(jq -r '.remoteDir // empty' "$CONFIG_FILE")
    NGINX_RELOAD=$(jq -r '.nginxReload // empty' "$CONFIG_FILE")
    DOMAIN=$(jq -r '.domain // empty' "$CONFIG_FILE")
fi

# 参数解析
SKIP_VERIFY=false
DRY_RUN=false
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-verify) SKIP_VERIFY=true; shift ;;
        --dry-run) DRY_RUN=true; shift ;;
        --message) MESSAGE="$2"; shift 2 ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
done

REMOTE="${USER}@${SERVER}"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

# 工具
ssh_cmd() {
    if [ "$DRY_RUN" = true ]; then
        echo "  [DRY-RUN] ssh $REMOTE $*"
    else
        ssh -i "$SSH_KEY" -p "$SSH_PORT" -o BatchMode=yes "$REMOTE" "$*"
    fi
}

scp_file() {
    local src=$1 dst=$2
    if [ "$DRY_RUN" = true ]; then
        echo "  [DRY-RUN] scp $src -> $REMOTE:$dst"
    else
        scp -i "$SSH_KEY" -P "$SSH_PORT" "$src" "${REMOTE}:${dst}"
    fi
}

color() { local c=$1; shift; printf "\033[%sm%s\033[0m\n" "$c" "$*"; }
green() { color "32" "$@"; }
cyan()  { color "36" "$@"; }
yellow(){ color "33" "$@"; }
red()   { color "31" "$@"; }
gray()  { color "90" "$@"; }
bold()  { color "1" "$@"; }

step() { echo ""; bold "▶ $*"; echo "────────────────────────────────────────────────────────────"; }

# ============================================================
# 1. 预检查
# ============================================================
step "预检查"

missing=()
for f in "${FILES[@]}"; do
    if [ ! -f "$PROJECT_ROOT/$f" ]; then
        missing+=("$f")
    fi
done
if [ ${#missing[@]} -gt 0 ]; then
    red "❌ 缺少文件: ${missing[*]}"
    exit 1
fi
green "✓ 所有 ${#FILES[@]} 个文件存在"

if [ ! -f "$SSH_KEY" ]; then
    red "❌ SSH 密钥不存在: $SSH_KEY"
    exit 1
fi
green "✓ SSH 密钥就绪"

if ! ssh_cmd "echo connected" | grep -q connected; then
    red "❌ 无法连接服务器"
    exit 1
fi
green "✓ 服务器可达"

nginx_status=$(ssh_cmd "systemctl is-active nginx" || true)
if [ "$nginx_status" = "active" ]; then
    green "✓ nginx 在运行"
else
    yellow "⚠️ nginx 状态: $nginx_status"
fi

# ============================================================
# 2. 打包
# ============================================================
step "打包本地文件"

STAGING_DIR="/tmp/posturemind-deploy-$TIMESTAMP"
rm -rf "$STAGING_DIR"
mkdir -p "$STAGING_DIR"

for f in "${FILES[@]}"; do
    cp "$PROJECT_ROOT/$f" "$STAGING_DIR/"
done

total_size=$(du -sk "$STAGING_DIR" | cut -f1)
green "✓ 已打包 ${#FILES[@]} 个文件，总大小 ${total_size} KB"

# ============================================================
# 3. 备份
# ============================================================
step "备份服务器当前版本"

BACKUP_NAME="posturemind-backup-$TIMESTAMP"
backup_result=$(ssh_cmd "if [ -d $REMOTE_DIR ]; then cp -r $REMOTE_DIR /tmp/$BACKUP_NAME && echo backed_up; else echo no_existing; fi")
if [[ "$backup_result" == *"backed_up"* ]]; then
    green "✓ 备份到 /tmp/$BACKUP_NAME"
else
    gray "→ 首次部署，无现有版本可备份"
fi

# ============================================================
# 4. 上传
# ============================================================
step "上传到服务器"

REMOTE_TMP="/tmp/posturemind-staging-$TIMESTAMP"
ssh_cmd "mkdir -p $REMOTE_TMP" >/dev/null

for f in "${FILES[@]}"; do
    scp_file "$STAGING_DIR/$f" "$REMOTE_TMP/$f"
done
green "✓ 上传完成"

# ============================================================
# 5. 部署
# ============================================================
step "部署到生产目录"

ssh_cmd "set -e
mkdir -p $REMOTE_DIR
cp -f $REMOTE_TMP/* $REMOTE_DIR/
chmod 644 $REMOTE_DIR/*
chown -R nginx:nginx $REMOTE_DIR 2>/dev/null || true
ls -la $REMOTE_DIR/
rm -rf $REMOTE_TMP"

green "✓ 已部署到 $REMOTE_DIR"

# ============================================================
# 6. 重载 nginx
# ============================================================
if [ "$NGINX_RELOAD" = true ]; then
    step "重载 nginx"
    ssh_cmd "nginx -t && systemctl reload nginx"
    green "✓ nginx 已重载"
fi

# ============================================================
# 7. 验证
# ============================================================
if [ "$SKIP_VERIFY" = false ]; then
    step "验证部署"

    http_status=$(ssh_cmd "curl -sk -o /dev/null -w '%{http_code}' ${DOMAIN}/")
    if [ "$http_status" = "200" ]; then
        green "✓ HTTPS 状态: $http_status"
    else
        yellow "⚠️ HTTPS 状态: $http_status"
    fi

    remote_size=$(ssh_cmd "curl -sk ${DOMAIN}/ | wc -c")
    local_size=$(wc -c < "$STAGING_DIR/index.html")
    gray "→ 远程: $remote_size 字节 | 本地: $local_size 字节"
    if [ "$remote_size" = "$local_size" ]; then
        green "✓ 大小一致"
    else
        yellow "⚠️ 大小不一致（可能是缓存）"
    fi

    for f in app.js knowledge.js styles.css; do
        status=$(ssh_cmd "curl -sk -o /dev/null -w '%{http_code}' ${DOMAIN}/$f")
        if [ "$status" = "200" ]; then
            green "  ✓ $f -> $status"
        else
            red "  ✗ $f -> $status"
        fi
    done
else
    gray "⏭ 跳过验证"
fi

# ============================================================
# 清理
# ============================================================
step "清理"
rm -rf "$STAGING_DIR"
green "✓ 临时文件已清理"

# ============================================================
# 完成
# ============================================================
echo ""
bold "═══════════════════════════════════════════════════════════"
bold "✓ 部署完成"
bold "═══════════════════════════════════════════════════════════"
echo ""
cyan "  🌐 ${DOMAIN}"
[ -n "$MESSAGE" ] && gray "  📝 $MESSAGE"
echo ""
gray "备份位置：/tmp/$BACKUP_NAME"
echo ""
gray "回滚命令："
echo "  ssh $REMOTE \"rm -rf $REMOTE_DIR && mv /tmp/$BACKUP_NAME $REMOTE_DIR\""
echo ""
