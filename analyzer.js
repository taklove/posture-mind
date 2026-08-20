// ============================================================
// 姿态分析器 —— 从 MediaPipe Pose 关键点检测各种体态问题
// ============================================================

class PostureAnalyzer {
  constructor() {
    // 关键 landmark 索引
    this.LM = {
      NOSE: 0,
      LEFT_EAR: 7, RIGHT_EAR: 8,
      LEFT_SHOULDER: 11, RIGHT_SHOULDER: 12,
      LEFT_ELBOW: 13, RIGHT_ELBOW: 14,
      LEFT_WRIST: 15, RIGHT_WRIST: 16,
      LEFT_HIP: 23, RIGHT_HIP: 24,
      LEFT_KNEE: 25, RIGHT_KNEE: 26,
      LEFT_ANKLE: 27, RIGHT_ANKLE: 28,
      LEFT_HEEL: 29, RIGHT_HEEL: 30,
      LEFT_FOOT_INDEX: 31, RIGHT_FOOT_INDEX: 32
    };
  }

  // 计算两点距离
  _dist(a, b) {
    return Math.hypot(a.x - b.x, a.y - b.y);
  }

  // 计算中点
  _mid(a, b) {
    return { x: (a.x + b.x) / 2, y: (a.y + b.y) / 2 };
  }

  // 向量
  _vec(a, b) {
    return { x: b.x - a.x, y: b.y - a.y };
  }

  // 向量夹角（度）
  _angleDeg(v1, v2) {
    const dot = v1.x * v2.x + v1.y * v2.y;
    const m1 = Math.hypot(v1.x, v1.y);
    const m2 = Math.hypot(v2.x, v2.y);
    const cos = Math.max(-1, Math.min(1, dot / (m1 * m2)));
    return Math.acos(cos) * 180 / Math.PI;
  }

  // ============================================================
  // 主体分析入口
  // input: 33 个 landmarks，每个有 x, y, z, visibility
  // view: 'front' | 'side' | 'back'
  // ============================================================
  analyze(landmarks, view) {
    if (!landmarks || landmarks.length < 33) {
      return { issues: [], summary: '未检测到人体关键点，请确保全身在画面内' };
    }

    // 取出关键点
    const L = {};
    for (const [k, idx] of Object.entries(this.LM)) {
      L[k] = landmarks[idx];
    }

    // 归一化参考：躯干长度（肩中点-髋中点）
    const shoulderMid = this._mid(L.LEFT_SHOULDER, L.RIGHT_SHOULDER);
    const hipMid = this._mid(L.LEFT_HIP, L.RIGHT_HIP);
    const torsoLen = this._dist(shoulderMid, hipMid) || 0.01;

    // 髋宽
    const hipWidth = this._dist(L.LEFT_HIP, L.RIGHT_HIP) || 0.01;

    // 肩宽
    const shoulderWidth = this._dist(L.LEFT_SHOULDER, L.RIGHT_SHOULDER) || 0.01;

    const issues = [];

    // 1. 高低肩（前面/后面）
    if (view === 'front' || view === 'back') {
      const shoulderHeightDiff = Math.abs(L.LEFT_SHOULDER.y - L.RIGHT_SHOULDER.y) / torsoLen;
      const hipHeightDiff = Math.abs(L.LEFT_HIP.y - L.RIGHT_HIP.y) / torsoLen;

      if (shoulderHeightDiff > 0.05) {
        const severity = shoulderHeightDiff > 0.10 ? '明显' : '轻度';
        issues.push({
          ...POSTURE_PATTERNS.shoulder_asymmetry,
          severity,
          score: shoulderHeightDiff,
          measurements: { shoulderHeightDiff: shoulderHeightDiff.toFixed(3) }
        });
      }
    }

    // 2. 头前伸 + 圆肩（侧面）
    if (view === 'side') {
      // 耳朵中心 vs 肩中点
      const earMid = this._mid(L.LEFT_EAR, L.RIGHT_EAR);
      const fhpOffset = (earMid.x - shoulderMid.x) / torsoLen;
      const shoulderForward = (shoulderMid.x - hipMid.x) / torsoLen;

      // 注意：图片坐标 y 向下，但 x 方向要看用户朝向
      // 这里假设用户面朝相机左侧（即"右侧面"对着相机）
      // 实际上需要根据情况判断方向，但简化为绝对值

      const fhpOffsetAbs = Math.abs(fhpOffset);
      const shoulderForwardAbs = Math.abs(shoulderForward);

      if (fhpOffsetAbs > 0.08 || shoulderForwardAbs > 0.10) {
        const severity = (fhpOffsetAbs > 0.15 || shoulderForwardAbs > 0.18) ? '明显' : '轻度';
        issues.push({
          ...POSTURE_PATTERNS.fhp_rounded_shoulder,
          severity,
          score: Math.max(fhpOffsetAbs, shoulderForwardAbs),
          measurements: {
            fhpOffset: fhpOffsetAbs.toFixed(3),
            shoulderForward: shoulderForwardAbs.toFixed(3)
          }
        });
      }
    }

    // 3. 骨盆前倾/后倾（侧面）
    if (view === 'side') {
      // 用髋-膝形成的角度与垂直线对比估算
      // 简化：髋部相对膝盖的水平偏移 + 髋部垂直位置
      const kneeMid = this._mid(L.LEFT_KNEE, L.RIGHT_KNEE);
      const hipKneeVec = this._vec(hipMid, kneeMid);
      const vertical = { x: 0, y: 1 };
      const pelvicAngle = this._angleDeg(hipKneeVec, vertical);

      // 估算腰椎曲度：肩膀-髋-膝 形成的"折角"
      const shoulderVec = this._vec(shoulderMid, hipMid);
      const lowerBackAngle = this._angleDeg(shoulderVec, hipKneeVec);

      if (pelvicAngle < 70) {
        // 骨盆前倾：髋-膝向量更倾斜
        issues.push({
          ...POSTURE_PATTERNS.anterior_pelvic_tilt,
          severity: pelvicAngle < 60 ? '明显' : '轻度',
          score: 80 - pelvicAngle,
          measurements: { pelvicAngle: pelvicAngle.toFixed(1) }
        });
      } else if (lowerBackAngle > 200) {
        issues.push({
          ...POSTURE_PATTERNS.posterior_pelvic_tilt,
          severity: lowerBackAngle > 215 ? '明显' : '轻度',
          score: lowerBackAngle - 200,
          measurements: { lowerBackAngle: lowerBackAngle.toFixed(1) }
        });
      }
    }

    // 4. 膝内扣（正面/背面）
    if (view === 'front' || view === 'back') {
      const kneeMid = this._mid(L.LEFT_KNEE, L.RIGHT_KNEE);
      const ankleMid = this._mid(L.LEFT_ANKLE, L.RIGHT_ANKLE);

      // 膝盖在髋-踝连线上的偏移
      const hipAnkleVec = this._vec(hipMid, ankleMid);
      const hipKneeVec = this._vec(hipMid, kneeMid);
      const kneeOffset = this._dist(kneeMid, ankleMid) / this._dist(hipMid, ankleMid);

      // 膝宽 / 髋宽 的比例
      const kneeWidth = this._dist(L.LEFT_KNEE, L.RIGHT_KNEE);
      const kneeHipRatio = kneeWidth / hipWidth;

      if (kneeHipRatio < 0.65 && kneeOffset > 0.05) {
        const severity = kneeHipRatio < 0.50 ? '明显' : '轻度';
        issues.push({
          ...POSTURE_PATTERNS.knee_valgus,
          severity,
          score: 0.65 - kneeHipRatio,
          measurements: {
            kneeHipRatio: kneeHipRatio.toFixed(2),
            kneeOffset: kneeOffset.toFixed(2)
          }
        });
      }
    }

    // 5. 膝过伸（侧面）
    if (view === 'side') {
      const kneeMid = this._mid(L.LEFT_KNEE, L.RIGHT_KNEE);
      const ankleMid = this._mid(L.LEFT_ANKLE, L.RIGHT_ANKLE);
      const hipKneeVec = this._vec(hipMid, kneeMid);
      const kneeAnkleVec = this._vec(kneeMid, ankleMid);
      const kneeAngle = this._angleDeg(hipKneeVec, kneeAnkleVec);

      if (kneeAngle > 185) {
        const severity = kneeAngle > 195 ? '明显' : '轻度';
        issues.push({
          ...POSTURE_PATTERNS.knee_hyperextension,
          severity,
          score: kneeAngle - 180,
          measurements: { kneeAngle: kneeAngle.toFixed(1) }
        });
      }
    }

    // 6. 头部侧倾（正面/背面）
    if (view === 'front' || view === 'back') {
      const earMid = this._mid(L.LEFT_EAR, L.RIGHT_EAR);
      const earHeightDiff = Math.abs(L.LEFT_EAR.y - L.RIGHT_EAR.y) / torsoLen;

      // 头部侧倾角度
      const earVec = this._vec(L.LEFT_EAR, L.RIGHT_EAR);
      const horizontal = { x: 1, y: 0 };
      const headTiltAngle = this._angleDeg(earVec, horizontal);

      if (earHeightDiff > 0.04 || headTiltAngle > 5) {
        const severity = (earHeightDiff > 0.08 || headTiltAngle > 10) ? '明显' : '轻度';
        issues.push({
          ...POSTURE_PATTERNS.head_lateral_tilt,
          severity,
          score: Math.max(earHeightDiff * 100, headTiltAngle),
          measurements: {
            earHeightDiff: earHeightDiff.toFixed(3),
            headTiltAngle: headTiltAngle.toFixed(1)
          }
        });
      }
    }

    // 7. 翼状肩胛（背面 - 简化检测）
    if (view === 'back') {
      // 简化：用肩胛骨区域（肩-脊柱中线距离）的不对称性
      // MediaPipe 没有直接的肩胛骨关键点，所以用肩部位置做粗略判断
      // 这里做一个基础检测，让用户知道要进一步评估
      const shoulderAsym = Math.abs(L.LEFT_SHOULDER.z - L.RIGHT_SHOULDER.z);
      if (shoulderAsym > 0.05) {
        issues.push({
          ...POSTURE_PATTERNS.winged_scapula,
          severity: '待确认',
          score: shoulderAsym,
          measurements: { shoulderDepthDiff: shoulderAsym.toFixed(3) },
          note: '建议做"推墙测试"确认：双手推墙时肩胛骨是否突出'
        });
      }
    }

    // 按严重度排序
    const severityOrder = { '明显': 3, '轻度': 2, '待确认': 1 };
    issues.sort((a, b) => (severityOrder[b.severity] || 0) - (severityOrder[a.severity] || 0));

    return {
      issues,
      summary: issues.length === 0
        ? '✓ 体态基本良好！继续保持规律训练。'
        : `检测到 ${issues.length} 项体态问题，建议针对训练。`,
      landmarks: L
    };
  }

  // ============================================================
  // 渲染姿态到 canvas（可视化）
  // ============================================================
  drawPose(ctx, landmarks, canvasWidth, canvasHeight) {
    if (!landmarks) return;

    // 骨骼连接
    const connections = [
      // 躯干
      [this.LM.LEFT_SHOULDER, this.LM.RIGHT_SHOULDER],
      [this.LM.LEFT_SHOULDER, this.LM.LEFT_HIP],
      [this.LM.RIGHT_SHOULDER, this.LM.RIGHT_HIP],
      [this.LM.LEFT_HIP, this.LM.RIGHT_HIP],
      // 头颈
      [this.LM.LEFT_EAR, this.LM.LEFT_SHOULDER],
      [this.LM.RIGHT_EAR, this.LM.RIGHT_SHOULDER],
      [this.LM.NOSE, this.LM.LEFT_EAR],
      [this.LM.NOSE, this.LM.RIGHT_EAR],
      // 手臂
      [this.LM.LEFT_SHOULDER, this.LM.LEFT_ELBOW],
      [this.LM.LEFT_ELBOW, this.LM.LEFT_WRIST],
      [this.LM.RIGHT_SHOULDER, this.LM.RIGHT_ELBOW],
      [this.LM.RIGHT_ELBOW, this.LM.RIGHT_WRIST],
      // 腿
      [this.LM.LEFT_HIP, this.LM.LEFT_KNEE],
      [this.LM.LEFT_KNEE, this.LM.LEFT_ANKLE],
      [this.LM.LEFT_ANKLE, this.LM.LEFT_HEEL],
      [this.LM.LEFT_HEEL, this.LM.LEFT_FOOT_INDEX],
      [this.LM.LEFT_ANKLE, this.LM.LEFT_FOOT_INDEX],
      [this.LM.RIGHT_HIP, this.LM.RIGHT_KNEE],
      [this.LM.RIGHT_KNEE, this.LM.RIGHT_ANKLE],
      [this.LM.RIGHT_ANKLE, this.LM.RIGHT_HEEL],
      [this.LM.RIGHT_HEEL, this.LM.RIGHT_FOOT_INDEX],
      [this.LM.RIGHT_ANKLE, this.LM.RIGHT_FOOT_INDEX]
    ];

    // 画骨骼
    ctx.lineWidth = 4;
    ctx.strokeStyle = '#0F766E';
    ctx.lineCap = 'round';

    for (const [a, b] of connections) {
      const pa = landmarks[a];
      const pb = landmarks[b];
      if (!pa || !pb) continue;
      if ((pa.visibility || 0) < 0.5 || (pb.visibility || 0) < 0.5) continue;
      ctx.beginPath();
      ctx.moveTo(pa.x * canvasWidth, pa.y * canvasHeight);
      ctx.lineTo(pb.x * canvasWidth, pb.y * canvasHeight);
      ctx.stroke();
    }

    // 画关键点
    const keyPoints = [
      this.LM.NOSE, this.LM.LEFT_EAR, this.LM.RIGHT_EAR,
      this.LM.LEFT_SHOULDER, this.LM.RIGHT_SHOULDER,
      this.LM.LEFT_ELBOW, this.LM.RIGHT_ELBOW,
      this.LM.LEFT_WRIST, this.LM.RIGHT_WRIST,
      this.LM.LEFT_HIP, this.LM.RIGHT_HIP,
      this.LM.LEFT_KNEE, this.LM.RIGHT_KNEE,
      this.LM.LEFT_ANKLE, this.LM.RIGHT_ANKLE
    ];

    ctx.fillStyle = '#F59E0B';
    for (const idx of keyPoints) {
      const p = landmarks[idx];
      if (!p || (p.visibility || 0) < 0.5) continue;
      ctx.beginPath();
      ctx.arc(p.x * canvasWidth, p.y * canvasHeight, 6, 0, Math.PI * 2);
      ctx.fill();
    }
  }
}

window.PostureAnalyzer = PostureAnalyzer;
