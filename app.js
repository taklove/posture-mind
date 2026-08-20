// ============================================================
// 正形 PostureMind —— 主应用
// ============================================================

class PostureMindApp {
  constructor() {
    this.analyzer = new PostureAnalyzer();
    this.poseLandmarker = null;
    this.video = null;
    this.canvas = null;
    this.ctx = null;
    this.currentView = 'front';
    this.currentScreen = 'loading';
    this.capturedFrames = { front: null, side: null, back: null };
    this.lastLandmarks = null;
    this.detectInterval = null;
    this.stream = null;
    this.detectionActive = false;

    // 当前诊断结果
    this.lastResult = null;

    // 训练状态
    this.currentExercise = null;
    this.exerciseTimer = null;
    this.timerSeconds = 0;

    // 训练完成记录（localStorage）
    this.completedToday = this.loadCompleted();

    this.init();
  }

  async init() {
    this.bindEvents();
    this.renderBottomNav();

    // 显示首页
    setTimeout(() => {
      this.goto('home');
      this.loadMediaPipeInBackground();
    }, 800);
  }

  // ============================================================
  // 后台加载 MediaPipe
  // ============================================================
  async loadMediaPipeInBackground() {
    try {
      const vision = await import('https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.14/vision_bundle.mjs');
      const { PoseLandmarker, FilesetResolver } = vision;

      const filesetResolver = await FilesetResolver.forVisionTasks(
        'https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.14/wasm'
      );

      this.poseLandmarker = await PoseLandmarker.createFromOptions(filesetResolver, {
        baseOptions: {
          modelAssetPath: 'https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task',
          delegate: 'GPU'
        },
        runningMode: 'IMAGE',
        numPoses: 1,
        minPoseDetectionConfidence: 0.5,
        minPosePresenceConfidence: 0.5,
        minTrackingConfidence: 0.5
      });

      console.log('✅ MediaPipe Pose ready');
      this.showToast('体态模型已就绪');
    } catch (err) {
      console.error('MediaPipe load failed:', err);
      this.showToast('模型加载失败，将使用基础分析');
    }
  }

  // ============================================================
  // 事件绑定
  // ============================================================
  bindEvents() {
    // 全局点击委托
    document.body.addEventListener('click', (e) => {
      const t = e.target.closest('[data-action]');
      if (t) {
        const action = t.dataset.action;
        if (action.startsWith('goto:')) {
          this.goto(action.slice(5));
        }
      }
    });

    // 底部导航
    document.querySelectorAll('.nav-item').forEach(item => {
      item.addEventListener('click', () => {
        const target = item.dataset.nav;
        this.goto(target);
      });
    });

    // 拍照页面：view 切换
    document.querySelectorAll('.view-tab').forEach(tab => {
      tab.addEventListener('click', () => {
        document.querySelectorAll('.view-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        this.currentView = tab.dataset.view;
        this.updateSilhouette();
      });
    });

    // 拍照按钮
    document.getElementById('captureBtn').addEventListener('click', () => this.captureFrame());
    document.getElementById('uploadBtn').addEventListener('click', () => {
      document.getElementById('fileInput').click();
    });
    document.getElementById('fileInput').addEventListener('change', (e) => this.handleFileUpload(e));
    document.getElementById('analyzeBtn').addEventListener('click', () => this.analyzeAll());
  }

  // ============================================================
  // 屏幕路由
  // ============================================================
  goto(screenName) {
    if (this.currentScreen === 'capture' && screenName !== 'capture') {
      this.stopCamera();
    }

    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    const target = document.querySelector(`[data-screen="${screenName}"]`);
    if (target) {
      target.classList.add('active');
      this.currentScreen = screenName;
    }

    // 底部导航 active 态
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    const navMap = { home: 'home', capture: 'capture', training: 'training', progress: 'progress' };
    const navKey = navMap[screenName];
    if (navKey) {
      const navEl = document.querySelector(`[data-nav="${navKey}"]`);
      if (navEl) navEl.classList.add('active');
    }

    // 进入不同屏幕的初始化
    if (screenName === 'capture') {
      this.startCamera();
    } else if (screenName === 'training') {
      this.renderTraining();
    } else if (screenName === 'progress') {
      this.renderProgress();
    }

    window.scrollTo(0, 0);
  }

  renderBottomNav() {
    // 已经在 HTML 里
  }

  // ============================================================
  // 相机 + 拍照
  // ============================================================
  async startCamera() {
    this.video = document.getElementById('video');
    this.canvas = document.getElementById('poseCanvas');
    this.ctx = this.canvas.getContext('2d');

    this.updateSilhouette();

    try {
      this.stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: 'environment',
          width: { ideal: 720 },
          height: { ideal: 960 }
        },
        audio: false
      });
      this.video.srcObject = this.stream;
      this.video.onloadedmetadata = () => {
        this.video.play();
        this.canvas.width = this.video.videoWidth;
        this.canvas.height = this.video.videoHeight;
        this.startDetection();
        this.updateCaptureStatus('📍 调整姿势，让全身入镜');
      };
    } catch (err) {
      console.error('Camera error:', err);
      this.updateCaptureStatus('⚠️ 无法访问相机，请使用上传功能');
      this.showToast('请允许相机权限，或使用上传照片');
    }
  }

  stopCamera() {
    this.detectionActive = false;
    if (this.detectInterval) {
      clearInterval(this.detectInterval);
      this.detectInterval = null;
    }
    if (this.stream) {
      this.stream.getTracks().forEach(t => t.stop());
      this.stream = null;
    }
    if (this.video) {
      this.video.srcObject = null;
    }
  }

  updateSilhouette() {
    const svg = document.getElementById('silhouette');
    if (!svg) return;
    const silhouettes = {
      front: `<circle cx="100" cy="40" r="18" stroke="white" stroke-width="2" fill="none"/>
              <line x1="100" y1="58" x2="100" y2="180" stroke="white" stroke-width="2"/>
              <line x1="100" y1="90" x2="60" y2="140" stroke="white" stroke-width="2"/>
              <line x1="100" y1="90" x2="140" y2="140" stroke="white" stroke-width="2"/>
              <line x1="100" y1="180" x2="80" y2="240" stroke="white" stroke-width="2"/>
              <line x1="100" y1="180" x2="120" y2="240" stroke="white" stroke-width="2"/>`,
      side: `<circle cx="100" cy="40" r="18" stroke="white" stroke-width="2" fill="none"/>
             <line x1="100" y1="58" x2="100" y2="180" stroke="white" stroke-width="2"/>
             <line x1="100" y1="90" x2="135" y2="140" stroke="white" stroke-width="2"/>
             <line x1="100" y1="90" x2="65" y2="140" stroke="white" stroke-width="2"/>
             <line x1="100" y1="180" x2="80" y2="240" stroke="white" stroke-width="2"/>
             <line x1="100" y1="180" x2="120" y2="240" stroke="white" stroke-width="2"/>`,
      back: `<circle cx="100" cy="40" r="18" stroke="white" stroke-width="2" fill="none"/>
             <line x1="100" y1="58" x2="100" y2="180" stroke="white" stroke-width="2"/>
             <line x1="100" y1="90" x2="60" y2="140" stroke="white" stroke-width="2"/>
             <line x1="100" y1="90" x2="140" y2="140" stroke="white" stroke-width="2"/>
             <line x1="100" y1="180" x2="80" y2="240" stroke="white" stroke-width="2"/>
             <line x1="100" y1="180" x2="120" y2="240" stroke="white" stroke-width="2"/>
             <!-- 脊柱线 -->
             <line x1="100" y1="58" x2="100" y2="180" stroke="#FCD34D" stroke-width="1" stroke-dasharray="2 2"/>`
    };
    svg.innerHTML = silhouettes[this.currentView] || silhouettes.front;
  }

  updateCaptureStatus(text) {
    const el = document.getElementById('captureStatus');
    if (el) el.textContent = text;
  }

  // ============================================================
  // 实时检测
  // ============================================================
  startDetection() {
    this.detectionActive = true;
    // 节流到每 200ms 检测一次
    this.detectInterval = setInterval(async () => {
      if (!this.detectionActive || !this.poseLandmarker || !this.video) return;
      if (this.video.readyState < 2) return;

      try {
        // 直接对 video element detect（MediaPipe IMAGE mode 支持）
        const result = this.poseLandmarker.detect(this.video);
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        if (result.landmarks && result.landmarks.length > 0) {
          const lm = result.landmarks[0];
          this.lastLandmarks = lm;
          this.analyzer.drawPose(this.ctx, lm, this.canvas.width, this.canvas.height);

          // 检查全身是否在画面内
          const fullBody = this.checkFullBody(lm);
          if (fullBody) {
            this.updateCaptureStatus('✓ 全身入镜，可以拍照');
          } else {
            this.updateCaptureStatus('⚠️ 请确保头顶到脚底都在画面内');
          }
        } else {
          this.updateCaptureStatus('🔍 寻找人体…');
        }
      } catch (err) {
        console.error('Detection error:', err);
      }
    }, 200);
  }

  checkFullBody(landmarks) {
    // 简单判断：鼻子、髋、膝、踝 visibility 都 > 0.5
    const indices = [0, 23, 24, 25, 26, 27, 28];
    return indices.every(i => landmarks[i] && (landmarks[i].visibility || 0) > 0.5);
  }

  // ============================================================
  // 拍照
  // ============================================================
  captureFrame() {
    if (!this.lastLandmarks) {
      this.showToast('未检测到人体，请调整位置');
      return;
    }
    if (!this.checkFullBody(this.lastLandmarks)) {
      this.showToast('请确保全身入镜');
      return;
    }

    // 抓取当前帧
    const captureCanvas = document.createElement('canvas');
    captureCanvas.width = this.video.videoWidth;
    captureCanvas.height = this.video.videoHeight;
    const cctx = captureCanvas.getContext('2d');
    cctx.drawImage(this.video, 0, 0);
    cctx.drawImage(this.canvas, 0, 0);

    this.capturedFrames[this.currentView] = {
      canvas: captureCanvas,
      landmarks: JSON.parse(JSON.stringify(this.lastLandmarks)),
      dataUrl: captureCanvas.toDataURL('image/jpeg', 0.85)
    };

    // 视觉反馈
    const flash = document.createElement('div');
    flash.style.cssText = 'position:fixed;inset:0;background:white;z-index:9999;pointer-events:none;';
    document.body.appendChild(flash);
    setTimeout(() => flash.remove(), 200);

    this.showToast(`✓ ${this.getViewName(this.currentView)}已拍摄`);

    // 自动切到下一个 view
    const views = ['front', 'side', 'back'];
    const idx = views.indexOf(this.currentView);
    if (idx < views.length - 1) {
      setTimeout(() => {
        this.currentView = views[idx + 1];
        document.querySelectorAll('.view-tab').forEach(t => t.classList.remove('active'));
        document.querySelector(`[data-view="${this.currentView}"]`).classList.add('active');
        this.updateSilhouette();
      }, 600);
    } else {
      // 全部拍完，启用分析按钮
      document.getElementById('analyzeBtn').disabled = false;
    }
  }

  getViewName(view) {
    return { front: '正面', side: '侧面', back: '背面' }[view] || view;
  }

  // ============================================================
  // 上传文件
  // ============================================================
  handleFileUpload(e) {
    const file = e.target.files[0];
    if (!file) return;

    const img = new Image();
    img.onload = async () => {
      // 把图缩放到画布
      const tmpCanvas = document.createElement('canvas');
      const maxW = 720;
      const scale = Math.min(1, maxW / img.width);
      tmpCanvas.width = img.width * scale;
      tmpCanvas.height = img.height * scale;
      const tctx = tmpCanvas.getContext('2d');
      tctx.drawImage(img, 0, 0, tmpCanvas.width, tmpCanvas.height);

      // 检测关键点
      if (this.poseLandmarker) {
        try {
          const result = this.poseLandmarker.detect(tmpCanvas);
          if (result.landmarks && result.landmarks.length > 0) {
            this.capturedFrames[this.currentView] = {
              canvas: tmpCanvas,
              landmarks: JSON.parse(JSON.stringify(result.landmarks[0])),
              dataUrl: tmpCanvas.toDataURL('image/jpeg', 0.85)
            };
            this.showToast(`✓ ${this.getViewName(this.currentView)}已上传`);

            // 自动切到下一个
            const views = ['front', 'side', 'back'];
            const idx = views.indexOf(this.currentView);
            if (idx < views.length - 1) {
              this.currentView = views[idx + 1];
              document.querySelectorAll('.view-tab').forEach(t => t.classList.remove('active'));
              document.querySelector(`[data-view="${this.currentView}"]`).classList.add('active');
              this.updateSilhouette();
            } else {
              document.getElementById('analyzeBtn').disabled = false;
            }
          } else {
            this.showToast('未检测到人体，请换张清晰的照片');
          }
        } catch (err) {
          this.showToast('分析失败：' + err.message);
        }
      } else {
        this.showToast('模型还在加载中，请稍候');
      }
    };
    img.src = URL.createObjectURL(file);
    e.target.value = '';
  }

  // ============================================================
  // 分析所有 view
  // ============================================================
  async analyzeAll() {
    const captured = this.capturedFrames;
    const count = Object.values(captured).filter(v => v).length;
    if (count === 0) {
      this.showToast('请先拍照或上传');
      return;
    }

    this.showToast('分析中…');

    const allIssues = [];
    const allLandmarks = {};
    const viewResults = {};

    for (const view of ['front', 'side', 'back']) {
      if (captured[view]) {
        const result = this.analyzer.analyze(captured[view].landmarks, view);
        viewResults[view] = result;
        allIssues.push(...result.issues);
        allLandmarks[view] = captured[view].landmarks;
      }
    }

    // 去重（按 id 保留严重度最高的）
    const issueMap = {};
    const severityRank = { '明显': 3, '轻度': 2, '待确认': 1 };
    for (const issue of allIssues) {
      const existing = issueMap[issue.id];
      if (!existing || severityRank[issue.severity] > severityRank[existing.severity]) {
        issueMap[issue.id] = issue;
      }
    }
    const finalIssues = Object.values(issueMap).sort((a, b) =>
      severityRank[b.severity] - severityRank[a.severity]
    );

    this.lastResult = {
      issues: finalIssues,
      captured,
      allLandmarks,
      timestamp: Date.now()
    };

    // 存到历史
    this.saveHistory(this.lastResult);

    this.renderResult();
    this.goto('result');
  }

  // ============================================================
  // 渲染结果
  // ============================================================
  renderResult() {
    if (!this.lastResult) return;

    const r = this.lastResult;
    const summaryEl = document.getElementById('resultSummary');
    const listEl = document.getElementById('issuesList');
    const canvas = document.getElementById('resultCanvas');

    // 总结
    if (r.issues.length === 0) {
      summaryEl.innerHTML = `
        <h2>🎉 你的体态很棒</h2>
        <p>未检测到明显的体态偏差，继续保持！</p>
      `;
    } else {
      const obviousCount = r.issues.filter(i => i.severity === '明显').length;
      const mildCount = r.issues.filter(i => i.severity === '轻度').length;
      summaryEl.innerHTML = `
        <h2>📋 检测到 ${r.issues.length} 项</h2>
        <p>${obviousCount ? `其中 <span style="color:#EF4444;font-weight:700">${obviousCount} 项</span> 需要重点关注，` : ''}${mildCount ? `${mildCount} 项轻度问题` : ''}</p>
        <p style="margin-top:8px;font-size:12px">下面卡片显示了 <strong style="color:#047857">真正需要唤醒的肌肉</strong></p>
      `;
    }

    // 画布：显示带骨骼的图
    const ctx = canvas.getContext('2d');
    const side = r.captured.side || r.captured.front || r.captured.back;
    if (side && side.canvas) {
      canvas.width = side.canvas.width;
      canvas.height = side.canvas.height;
      ctx.drawImage(side.canvas, 0, 0);
      // 找对应 view
      const view = r.captured.side ? 'side' : (r.captured.front ? 'front' : 'back');
      if (r.allLandmarks[view]) {
        this.analyzer.drawPose(ctx, r.allLandmarks[view], canvas.width, canvas.height);
      }
    }

    // 问题列表
    if (r.issues.length === 0) {
      listEl.innerHTML = `
        <div class="issue-card">
          <div class="issue-header">
            <div class="issue-title"><span class="issue-icon">✨</span><span>体态优秀</span></div>
          </div>
          <p class="issue-desc">没发现需要优先解决的问题。继续保持运动习惯，建议每周 2-3 次核心 + 臀部训练。</p>
        </div>
      `;
    } else {
      listEl.innerHTML = r.issues.map(issue => this.renderIssueCard(issue)).join('');
    }
  }

  renderIssueCard(issue) {
    const severityClass = {
      '明显': 'severity-obvious',
      '轻度': 'severity-mild',
      '待确认': 'severity-check'
    }[issue.severity] || 'severity-mild';

    return `
      <div class="issue-card ${severityClass}">
        <div class="issue-header">
          <div class="issue-title">
            <span class="issue-icon">${issue.icon}</span>
            <span>${issue.name}</span>
          </div>
          <span class="issue-severity ${severityClass}">${issue.severity}</span>
        </div>
        <p class="issue-desc">${issue.shortDesc}</p>

        <div class="muscle-section compensating">
          <div class="muscle-label">🔴 代偿肌（看起来紧的 — 不是问题根源）</div>
          <div class="muscle-list">
            ${issue.compensatingMuscles.map(m => `<span class="muscle-chip">${m.name}</span>`).join('')}
          </div>
        </div>

        <div class="muscle-section rootcause" style="margin-top:8px">
          <div class="muscle-label">🟢 该发力却没发力的（这才是根源）</div>
          <div class="muscle-list">
            ${issue.shouldBeStrong.map(m => `<span class="muscle-chip">${m.name}</span>`).join('')}
          </div>
        </div>
      </div>
    `;
  }

  // ============================================================
  // 训练页
  // ============================================================
  renderTraining() {
    const listEl = document.getElementById('exerciseList');
    const summaryEl = document.getElementById('trainingSummary');

    if (!this.lastResult || this.lastResult.issues.length === 0) {
      summaryEl.innerHTML = `
        <h3>🎉 没有需要优先训练的问题</h3>
        <p>建议每周保持 2-3 次核心和臀部训练做预防性强化</p>
      `;

      // 显示通用训练
      const generic = [
        ...EXERCISES.core.slice(0, 2),
        ...EXERCISES.gluteMax.slice(0, 1),
        ...EXERCISES.lowerTrap_serratus.slice(0, 1)
      ];
      listEl.innerHTML = generic.map(ex => this.renderExerciseCard(ex)).join('');
    } else {
      // 根据问题生成训练列表
      const plan = this.buildTrainingPlan(this.lastResult.issues);
      const issueNames = this.lastResult.issues.map(i => i.name).join('、');
      summaryEl.innerHTML = `
        <h3>💪 针对你的 ${this.lastResult.issues.length} 项体态问题</h3>
        <p>${issueNames}</p>
        <p style="margin-top:8px;opacity:0.85">每天 10 分钟 · 唤醒沉睡的肌肉</p>
      `;
      listEl.innerHTML = plan.map(ex => this.renderExerciseCard(ex)).join('');
    }

    this.bindExerciseEvents();
    this.updateProgress();
  }

  buildTrainingPlan(issues) {
    const plan = [];
    const usedMuscleGroups = new Set();

    for (const issue of issues) {
      const treatment = TREATMENT_MAP[issue.id];
      if (!treatment) continue;
      for (const group of [...treatment.primary, ...treatment.secondary]) {
        if (usedMuscleGroups.has(group)) continue;
        usedMuscleGroups.add(group);
        const exs = EXERCISES[group] || [];
        for (const ex of exs) {
          if (plan.find(p => p.id === ex.id)) continue;
          plan.push(ex);
        }
      }
    }

    // 每个肌肉组最多 1-2 个动作
    return plan.slice(0, 6);
  }

  renderExerciseCard(ex) {
    const done = this.completedToday[ex.id];
    return `
      <div class="exercise-card ${done ? 'done' : ''}" data-exercise-id="${ex.id}">
        <div class="exercise-thumb">${this.getExerciseEmoji(ex.id)}</div>
        <div class="exercise-info">
          <p class="exercise-name">${ex.name}</p>
          <p class="exercise-target">${ex.target}</p>
          <p class="exercise-meta">
            <span>${ex.sets} 组</span>
            <span>${ex.reps}</span>
          </p>
        </div>
        <button class="exercise-check ${done ? 'checked' : ''}" data-check-id="${ex.id}">${done ? '✓' : ''}</button>
      </div>
    `;
  }

  getExerciseEmoji(id) {
    const map = {
      chin_tuck: '😐', supine_head_lift: '😴',
      prone_y_raise: '✋', wall_slide: '🧱', scap_pushup: '🤲',
      glute_bridge: '🌉', single_leg_bridge: '🦵',
      clamshell: '🐚', side_leg_raise: '🦿',
      dead_bug: '🪲', bird_dog: '🐕',
      glute_bridge_curl: '⚽',
      tke: '🦵', short_foot: '🦶',
      '90_90_breath': '🫁'
    };
    return map[id] || '💪';
  }

  bindExerciseEvents() {
    document.querySelectorAll('.exercise-card').forEach(card => {
      card.addEventListener('click', (e) => {
        if (e.target.closest('.exercise-check')) return;
        const id = card.dataset.exerciseId;
        this.openExercise(id);
      });
    });
    document.querySelectorAll('.exercise-check').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const id = btn.dataset.checkId;
        this.toggleExerciseDone(id);
      });
    });
  }

  findExercise(id) {
    for (const group of Object.values(EXERCISES)) {
      const found = group.find(e => e.id === id);
      if (found) return found;
    }
    return null;
  }

  openExercise(id) {
    const ex = this.findExercise(id);
    if (!ex) return;
    this.currentExercise = ex;
    this.renderExerciseDetail(ex);
    this.goto('exercise');
  }

  renderExerciseDetail(ex) {
    document.getElementById('exerciseTitle').textContent = ex.name;
    const detail = document.getElementById('exerciseDetail');
    const svg = EXERCISE_SVGS[ex.svg] || EXERCISE_SVGS.bridge;
    const done = this.completedToday[ex.id];

    detail.innerHTML = `
      <div class="exercise-hero">
        <h2>${ex.name}</h2>
        <p class="target">针对：${ex.target}</p>
      </div>

      <div class="exercise-svg">${svg}</div>

      <div class="exercise-prescription">
        <div class="prescription-item">
          <span class="num">${ex.sets}</span>
          <span class="lbl">组数</span>
        </div>
        <div class="prescription-item">
          <span class="num">${ex.reps.replace(/[^\d]/g, '') || '1'}</span>
          <span class="lbl">${ex.reps.includes('次') ? '次' : '次/秒'}</span>
        </div>
        <div class="prescription-item">
          <span class="num">L${ex.level}</span>
          <span class="lbl">难度</span>
        </div>
      </div>

      <div class="cues-section">
        <h3>✓ 动作要点</h3>
        <ul class="cues-list">
          ${ex.cues.map(c => `<li>${c}</li>`).join('')}
        </ul>
      </div>

      ${ex.avoid && ex.avoid.length ? `
        <div class="cues-section">
          <h3>✗ 避免错误</h3>
          <ul class="cues-list avoid-list">
            ${ex.avoid.map(c => `<li>${c}</li>`).join('')}
          </ul>
        </div>
      ` : ''}

      <div class="timer-section">
        <p style="margin:0;font-size:13px;color:var(--text-light)">计时器</p>
        <div class="timer-display" id="timerDisplay">00:30</div>
        <div class="timer-controls">
          <button class="timer-btn" data-timer="add">+10s</button>
          <button class="timer-btn primary" data-timer="start">开始</button>
          <button class="timer-btn" data-timer="reset">重置</button>
        </div>
      </div>

      <button class="mark-done-btn" data-mark-done="${ex.id}">
        ${done ? '✓ 已完成（再点取消）' : '标记完成 ✓'}
      </button>
    `;

    // 计时器
    this.timerSeconds = 30;
    this.updateTimerDisplay();
    document.querySelectorAll('[data-timer]').forEach(btn => {
      btn.addEventListener('click', () => this.handleTimer(btn.dataset.timer));
    });
    document.querySelector('[data-mark-done]').addEventListener('click', (e) => {
      this.toggleExerciseDone(e.currentTarget.dataset.markDone);
      const isDone = this.completedToday[e.currentTarget.dataset.markDone];
      e.currentTarget.textContent = isDone ? '✓ 已完成（再点取消）' : '标记完成 ✓';
    });
  }

  handleTimer(action) {
    if (action === 'start') {
      if (this.exerciseTimer) {
        clearInterval(this.exerciseTimer);
        this.exerciseTimer = null;
        document.querySelector('[data-timer="start"]').textContent = '开始';
        return;
      }
      document.querySelector('[data-timer="start"]').textContent = '暂停';
      this.exerciseTimer = setInterval(() => {
        this.timerSeconds--;
        this.updateTimerDisplay();
        if (this.timerSeconds <= 0) {
          clearInterval(this.exerciseTimer);
          this.exerciseTimer = null;
          document.querySelector('[data-timer="start"]').textContent = '开始';
          this.showToast('⏰ 时间到！');
          this.vibrate(200);
        }
      }, 1000);
    } else if (action === 'add') {
      this.timerSeconds += 10;
      this.updateTimerDisplay();
    } else if (action === 'reset') {
      if (this.exerciseTimer) {
        clearInterval(this.exerciseTimer);
        this.exerciseTimer = null;
      }
      this.timerSeconds = 30;
      this.updateTimerDisplay();
      document.querySelector('[data-timer="start"]').textContent = '开始';
    }
  }

  updateTimerDisplay() {
    const el = document.getElementById('timerDisplay');
    if (!el) return;
    const m = Math.floor(this.timerSeconds / 60);
    const s = this.timerSeconds % 60;
    el.textContent = `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  }

  // ============================================================
  // 完成状态管理
  // ============================================================
  toggleExerciseDone(id) {
    if (this.completedToday[id]) {
      delete this.completedToday[id];
    } else {
      this.completedToday[id] = Date.now();
      this.vibrate(50);
    }
    this.saveCompleted();
    this.updateProgress();
    // 更新当前列表
    if (this.currentScreen === 'training') {
      this.renderTraining();
    }
  }

  updateProgress() {
    const total = document.querySelectorAll('.exercise-card').length;
    const done = Object.keys(this.completedToday).filter(id => {
      return document.querySelector(`[data-exercise-id="${id}"]`) !== null;
    }).length;
    const pct = total ? (done / total * 100) : 0;
    const fill = document.getElementById('progressFill');
    const text = document.getElementById('progressText');
    if (fill) fill.style.width = pct + '%';
    if (text) text.textContent = `${done} / ${total} 完成`;
  }

  loadCompleted() {
    try {
      const saved = localStorage.getItem('pm_completed_' + this.getTodayKey());
      return saved ? JSON.parse(saved) : {};
    } catch {
      return {};
    }
  }

  saveCompleted() {
    try {
      localStorage.setItem('pm_completed_' + this.getTodayKey(), JSON.stringify(this.completedToday));
    } catch {}
  }

  getTodayKey() {
    const d = new Date();
    return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
  }

  // ============================================================
  // 历史记录
  // ============================================================
  saveHistory(result) {
    try {
      const key = 'pm_history';
      const history = JSON.parse(localStorage.getItem(key) || '[]');
      const entry = {
        timestamp: result.timestamp,
        issues: result.issues.map(i => ({
          id: i.id,
          name: i.name,
          severity: i.severity,
          icon: i.icon
        }))
      };
      history.unshift(entry);
      localStorage.setItem(key, JSON.stringify(history.slice(0, 20)));
    } catch {}
  }

  loadHistory() {
    try {
      return JSON.parse(localStorage.getItem('pm_history') || '[]');
    } catch {
      return [];
    }
  }

  renderProgress() {
    const el = document.getElementById('progressContent');
    const history = this.loadHistory();

    if (history.length === 0) {
      el.innerHTML = `
        <div class="empty-state">
          <div class="icon">📈</div>
          <h3>还没有记录</h3>
          <p>完成一次体态评估，开始跟踪你的变化</p>
          <button class="btn btn-primary" style="margin-top:20px" data-action="goto:capture">开始评估</button>
        </div>
      `;
      return;
    }

    el.innerHTML = `
      <h3 style="margin:0 0 16px">评估历史</h3>
      <div class="history-list">
        ${history.map((h, idx) => `
          <div class="history-item">
            <div class="history-date">${new Date(h.timestamp).toLocaleString('zh-CN')}</div>
            <div class="history-issues">
              ${h.issues.length === 0
                ? '✨ 无明显问题'
                : h.issues.map(i => `${i.icon} ${i.name} <span style="font-size:11px;opacity:0.7">(${i.severity})</span>`).join(' · ')
              }
            </div>
          </div>
        `).join('')}
      </div>

      <div style="margin-top:32px;padding:20px;background:linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);border-radius:16px">
        <h3 style="margin:0 0 8px">📊 训练连续打卡</h3>
        <p style="margin:0;font-size:14px;color:#92400E">建议每周至少 3 次针对性训练。规律比强度更重要。</p>
      </div>
    `;
  }

  // ============================================================
  // 工具
  // ============================================================
  showToast(text, duration = 2200) {
    const toast = document.getElementById('toast');
    toast.textContent = text;
    toast.classList.add('show');
    clearTimeout(this._toastTimer);
    this._toastTimer = setTimeout(() => toast.classList.remove('show'), duration);
  }

  vibrate(ms) {
    if (navigator.vibrate) navigator.vibrate(ms);
  }
}

// 启动
document.addEventListener('DOMContentLoaded', () => {
  window.app = new PostureMindApp();
});
