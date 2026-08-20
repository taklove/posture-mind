// ============================================================
// 动作 SVG 插画库
// 每个动作一个简洁的矢量图
// ============================================================

const EXERCISE_SVGS = {
  chinTuck: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 人侧脸面对右 -->
      <!-- 头部 -->
      <circle cx="80" cy="60" r="22" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 脖子 (收下巴，脖子后侧变短) -->
      <path d="M 95 78 L 100 95 L 105 78 Z" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 躯干 -->
      <rect x="90" y="95" width="30" height="60" fill="#0F766E" rx="6"/>
      <!-- 箭头：下巴往后收 -->
      <line x1="100" y1="55" x2="135" y2="55" stroke="#EF4444" stroke-width="3" marker-end="url(#arrow)"/>
      <defs>
        <marker id="arrow" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto">
          <path d="M0,0 L0,6 L9,3 z" fill="#EF4444"/>
        </marker>
      </defs>
      <text x="140" y="50" font-size="12" fill="#EF4444" font-weight="700">收</text>
    </svg>
  `,

  headLift: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 地面 -->
      <line x1="20" y1="160" x2="180" y2="160" stroke="#64748B" stroke-width="2"/>
      <!-- 仰卧身体 -->
      <ellipse cx="100" cy="140" rx="50" ry="12" fill="#0F766E"/>
      <!-- 头部抬起 -->
      <circle cx="100" cy="100" r="18" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 弯腿 -->
      <path d="M 130 145 L 145 130 L 145 160" stroke="#92400E" stroke-width="3" fill="none"/>
      <path d="M 75 145 L 60 130 L 60 160" stroke="#92400E" stroke-width="3" fill="none"/>
      <!-- 箭头 -->
      <line x1="100" y1="125" x2="100" y2="105" stroke="#10B981" stroke-width="3" marker-end="url(#ar2)"/>
      <defs>
        <marker id="ar2" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto">
          <path d="M0,0 L0,6 L9,3 z" fill="#10B981"/>
        </marker>
      </defs>
    </svg>
  `,

  proneY: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 地面 -->
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 俯卧身体 -->
      <ellipse cx="100" cy="160" rx="60" ry="8" fill="#0F766E"/>
      <!-- 头 -->
      <circle cx="50" cy="155" r="12" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- Y 形手臂 -->
      <line x1="55" y1="158" x2="30" y2="100" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
      <line x1="55" y1="158" x2="80" y2="100" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
      <line x1="50" y1="160" x2="50" y2="160" stroke="#F59E0B" stroke-width="4"/>
      <!-- 拇指标记 -->
      <circle cx="30" cy="100" r="4" fill="#F59E0B"/>
      <circle cx="80" cy="100" r="4" fill="#F59E0B"/>
    </svg>
  `,

  wallSlide: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 墙 -->
      <line x1="40" y1="20" x2="40" y2="180" stroke="#64748B" stroke-width="3" stroke-dasharray="4 2"/>
      <!-- 人 -->
      <circle cx="80" cy="50" r="16" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 身体 -->
      <rect x="72" y="65" width="16" height="60" fill="#0F766E" rx="4"/>
      <!-- W 形手臂贴墙 -->
      <line x1="80" y1="70" x2="50" y2="85" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
      <line x1="50" y1="85" x2="50" y2="110" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
      <line x1="80" y1="70" x2="50" y2="110" stroke="#F59E0B" stroke-width="3" stroke-linecap="round" opacity="0.5"/>
      <!-- 腿 -->
      <line x1="75" y1="125" x2="70" y2="180" stroke="#92400E" stroke-width="4"/>
      <line x1="85" y1="125" x2="90" y2="180" stroke="#92400E" stroke-width="4"/>
    </svg>
  `,

  scapPushup: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 地面 -->
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 俯撑身体 -->
      <line x1="40" y1="170" x2="160" y2="120" stroke="#0F766E" stroke-width="14" stroke-linecap="round"/>
      <!-- 头 -->
      <circle cx="40" cy="170" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 手臂撑地 -->
      <line x1="40" y1="170" x2="55" y2="160" stroke="#92400E" stroke-width="4" stroke-linecap="round"/>
      <!-- 肩胛骨挤压 -->
      <path d="M 100 110 Q 95 100 105 100" stroke="#EF4444" stroke-width="2" fill="none" stroke-dasharray="3 2"/>
      <text x="120" y="105" font-size="11" fill="#EF4444" font-weight="700">肩胛向中</text>
    </svg>
  `,

  bridge: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 地面 -->
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 弯腿 -->
      <path d="M 50 170 L 50 130 L 80 130 L 80 170" stroke="#92400E" stroke-width="3" fill="none"/>
      <!-- 拱起身体 -->
      <path d="M 50 130 Q 100 70 150 130" stroke="#0F766E" stroke-width="14" fill="none" stroke-linecap="round"/>
      <!-- 头 -->
      <circle cx="160" cy="135" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 臀大肌标记 -->
      <circle cx="75" cy="105" r="8" fill="#EF4444" opacity="0.6"/>
      <text x="30" y="100" font-size="11" fill="#EF4444" font-weight="700">夹臀</text>
    </svg>
  `,

  singleLegBridge: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <path d="M 50 170 L 50 130 L 80 130 L 80 170" stroke="#92400E" stroke-width="3" fill="none"/>
      <path d="M 50 130 Q 100 70 150 130" stroke="#0F766E" stroke-width="14" fill="none" stroke-linecap="round"/>
      <circle cx="160" cy="135" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 伸直的腿 -->
      <line x1="80" y1="130" x2="50" y2="60" stroke="#0F766E" stroke-width="6" stroke-linecap="round"/>
      <text x="30" y="55" font-size="11" fill="#10B981" font-weight="700">伸直</text>
    </svg>
  `,

  clamshell: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 地面 -->
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 侧卧身体 -->
      <ellipse cx="100" cy="140" rx="50" ry="10" fill="#0F766E"/>
      <!-- 头 -->
      <circle cx="55" cy="135" r="12" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 双腿像贝壳打开 -->
      <line x1="135" y1="140" x2="155" y2="100" stroke="#92400E" stroke-width="5" stroke-linecap="round"/>
      <line x1="135" y1="140" x2="155" y2="170" stroke="#92400E" stroke-width="5" stroke-linecap="round"/>
      <!-- 开合箭头 -->
      <path d="M 145 110 L 145 130" stroke="#EF4444" stroke-width="2" marker-end="url(#ar3)"/>
      <defs>
        <marker id="ar3" markerWidth="8" markerHeight="8" refX="6" refY="2" orient="auto">
          <path d="M0,0 L0,4 L6,2 z" fill="#EF4444"/>
        </marker>
      </defs>
    </svg>
  `,

  sideLegRaise: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <ellipse cx="100" cy="140" rx="50" ry="10" fill="#0F766E"/>
      <circle cx="55" cy="135" r="12" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 抬起的腿 -->
      <line x1="135" y1="140" x2="170" y2="90" stroke="#0F766E" stroke-width="6" stroke-linecap="round"/>
      <!-- 箭头 -->
      <line x1="160" y1="135" x2="160" y2="100" stroke="#EF4444" stroke-width="2" stroke-dasharray="3 2"/>
    </svg>
  `,

  deadBug: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 仰卧 -->
      <ellipse cx="100" cy="140" rx="55" ry="10" fill="#0F766E"/>
      <circle cx="55" cy="135" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 对侧手脚伸出 -->
      <!-- 手往后 -->
      <line x1="55" y1="135" x2="20" y2="120" stroke="#92400E" stroke-width="4" stroke-linecap="round"/>
      <!-- 脚往前 -->
      <line x1="135" y1="140" x2="175" y2="155" stroke="#92400E" stroke-width="4" stroke-linecap="round"/>
      <line x1="135" y1="140" x2="135" y2="110" stroke="#92400E" stroke-width="4" stroke-linecap="round"/>
      <text x="155" y="100" font-size="10" fill="#10B981" font-weight="700">90°</text>
    </svg>
  `,

  birdDog: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 四点跪姿 -->
      <line x1="50" y1="170" x2="100" y2="120" stroke="#0F766E" stroke-width="12" stroke-linecap="round"/>
      <!-- 头 -->
      <circle cx="50" cy="170" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 支撑手 -->
      <line x1="50" y1="170" x2="40" y2="155" stroke="#92400E" stroke-width="4"/>
      <!-- 支撑膝 -->
      <line x1="100" y1="120" x2="110" y2="170" stroke="#92400E" stroke-width="4"/>
      <!-- 伸出对侧 -->
      <line x1="50" y1="170" x2="30" y2="110" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
      <line x1="110" y1="170" x2="170" y2="120" stroke="#F59E0B" stroke-width="4" stroke-linecap="round"/>
    </svg>
  `,

  breathing: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <ellipse cx="100" cy="140" rx="55" ry="10" fill="#0F766E"/>
      <circle cx="55" cy="135" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 90/90 腿 -->
      <line x1="135" y1="140" x2="135" y2="100" stroke="#92400E" stroke-width="4"/>
      <line x1="135" y1="100" x2="165" y2="100" stroke="#92400E" stroke-width="4"/>
      <!-- 呼吸波 -->
      <path d="M 70 90 Q 90 80 100 90 Q 110 100 130 90" stroke="#10B981" stroke-width="2" fill="none"/>
      <text x="65" y="70" font-size="10" fill="#10B981" font-weight="700">360° 鼓起</text>
    </svg>
  `,

  bridgeCurl: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 球 -->
      <circle cx="160" cy="170" r="20" fill="#F59E0B"/>
      <path d="M 50 170 L 50 130 L 80 130 L 80 170" stroke="#92400E" stroke-width="3" fill="none"/>
      <!-- 拱起身体 -->
      <path d="M 50 130 Q 100 70 150 130" stroke="#0F766E" stroke-width="14" fill="none" stroke-linecap="round"/>
      <!-- 腿伸直踩在球上 -->
      <line x1="80" y1="130" x2="155" y2="155" stroke="#0F766E" stroke-width="6" stroke-linecap="round"/>
    </svg>
  `,

  tke: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <!-- 弹力带 -->
      <line x1="20" y1="100" x2="100" y2="100" stroke="#EF4444" stroke-width="3"/>
      <!-- 腿 -->
      <circle cx="130" cy="60" r="10" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <line x1="130" y1="70" x2="130" y2="120" stroke="#0F766E" stroke-width="10" stroke-linecap="round"/>
      <line x1="130" y1="120" x2="130" y2="160" stroke="#0F766E" stroke-width="10" stroke-linecap="round"/>
      <!-- 膝盖在弹力带中 -->
      <circle cx="100" cy="100" r="8" fill="#F59E0B"/>
      <!-- 箭头：膝盖外推 -->
      <line x1="80" y1="100" x2="105" y2="100" stroke="#10B981" stroke-width="3" marker-end="url(#ar4)"/>
      <defs>
        <marker id="ar4" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto">
          <path d="M0,0 L0,6 L9,3 z" fill="#10B981"/>
        </marker>
      </defs>
    </svg>
  `,

  shortFoot: `
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
      <line x1="20" y1="170" x2="180" y2="170" stroke="#64748B" stroke-width="2"/>
      <!-- 脚的侧面 -->
      <ellipse cx="100" cy="160" rx="50" ry="12" fill="#FCD34D" stroke="#92400E" stroke-width="2"/>
      <!-- 足弓 -->
      <path d="M 80 160 Q 100 140 120 160" stroke="#EF4444" stroke-width="2" fill="none" stroke-dasharray="3 2"/>
      <!-- 箭头：足弓上提 -->
      <line x1="100" y1="155" x2="100" y2="130" stroke="#10B981" stroke-width="3" marker-end="url(#ar5)"/>
      <text x="85" y="125" font-size="10" fill="#10B981" font-weight="700">提足弓</text>
      <defs>
        <marker id="ar5" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto">
          <path d="M0,0 L0,6 L9,3 z" fill="#10B981"/>
        </marker>
      </defs>
    </svg>
  `
};

window.EXERCISE_SVGS = EXERCISE_SVGS;
