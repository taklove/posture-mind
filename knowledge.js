// ============================================================
// 正形 PostureMind —— 核心知识库
// 哲学：体态问题的根源，是「该发力却没发力的肌肉」被抑制
//       而「正在代偿的肌肉」过度紧张 —— 治疗方向是唤醒弱者
// ============================================================

// 每种体态问题 = 一个 pattern
// 每个 pattern 包含：
//   - 代偿肌（看起来紧张 / 过度工作）—— 这些不是问题，是症状
//   - 应发力肌（应该工作但被抑制）—— 这些才是问题根源
//   - 视觉特征（从照片/视频能直接看到的）
//   - 检测信号（从 MediaPipe landmarks 怎么量化）

const POSTURE_PATTERNS = {
  // ============================================================
  // 1. 上交叉综合征 —— 头前伸 + 圆肩
  // ============================================================
  fhp_rounded_shoulder: {
    id: 'fhp_rounded_shoulder',
    name: '头前伸 + 圆肩',
    nameEn: 'Forward Head & Rounded Shoulders',
    shortDesc: '耳朵在肩膀前方，肩膀在胸前',
    icon: '🔄',
    views: ['side', 'front'],
    compensatingMuscles: [
      { name: '上斜方肌', reason: '代偿抬头，肩颈僵硬' },
      { name: '胸锁乳突肌', reason: '代偿屈颈，脖子前面紧' },
      { name: '胸大肌 / 胸小肌', reason: '代偿肩内旋，胸前紧' },
      { name: '肩胛提肌', reason: '代偿提肩' }
    ],
    shouldBeStrong: [
      { name: '颈深屈肌（颈长肌、头长肌）', role: '稳定颈椎，让头回到肩膀正上方' },
      { name: '下斜方肌', role: '把肩胛骨向下拉，对抗耸肩' },
      { name: '前锯肌', role: '把肩胛骨贴紧胸廓，对抗圆肩' },
      { name: '中斜方肌 + 菱形肌', role: '把肩胛骨向脊柱拉，对抗前伸' },
      { name: '肩外旋肌（冈下肌、小圆肌）', role: '让肱骨头回到肩窝中心' }
    ],
    visualSignals: [
      '从侧面看：耳朵在肩膀垂线的前方',
      '从侧面看：肩峰在髋部垂线的前方',
      '从正面看：肩膀高度不一致 / 锁骨外侧上翘',
      '脖子后面有"富贵包"或皮肤褶皱'
    ],
    detection: {
      // 侧面：耳朵 (0) 相对肩膀中点 (11/12) 的水平偏移，归一化用躯干长
      fhp_ratio: { threshold: 0.10, weight: 0.5 },     // 耳-肩水平距离 / 躯干长
      shoulder_forward: { threshold: 0.12, weight: 0.5 } // 肩-髋水平距离 / 躯干长
    }
  },

  // ============================================================
  // 2. 下交叉综合征 —— 骨盆前倾 + 腰椎过伸
  // ============================================================
  anterior_pelvic_tilt: {
    id: 'anterior_pelvic_tilt',
    name: '骨盆前倾（腰椎过伸）',
    nameEn: 'Anterior Pelvic Tilt',
    shortDesc: '下背部弧度过大，小肚子突出',
    icon: '🔃',
    views: ['side'],
    compensatingMuscles: [
      { name: '髂腰肌', reason: '代偿屈髋，让骨盆被拉向前倾' },
      { name: '股直肌', reason: '代偿屈髋 + 伸膝' },
      { name: '竖脊肌（腰部）', reason: '代偿稳定腰椎，过度紧张' }
    ],
    shouldBeStrong: [
      { name: '臀大肌', role: '骨盆后倾的原动力 —— 这是关键' },
      { name: '腘绳肌', role: '辅助骨盆后倾' },
      { name: '腹横肌', role: '腹内压核心，让骨盆不被拉前' },
      { name: '多裂肌', role: '深层稳定腰椎' },
      { name: '臀中肌', role: '稳定骨盆水平面' }
    ],
    visualSignals: [
      '从侧面看：髋部前缘比髋部后缘更靠前',
      '从侧面看：腰椎弧度明显（腰窝很深）',
      '小肚子突出（不是胖，是骨盆前倾顶出来的）',
      '屁股看起来很翘（但不是真的翘，是前倾造成的视觉）'
    ],
    detection: {
      // 用髋-膝-踝形成的角度估算，或髋部倾斜
      pelvic_tilt_angle: { threshold: 15, weight: 0.6 }, // 度
      lower_back_curve: { threshold: 0.15, weight: 0.4 }
    }
  },

  // ============================================================
  // 3. 骨盆后倾 —— 与前倾相反
  // ============================================================
  posterior_pelvic_tilt: {
    id: 'posterior_pelvic_tilt',
    name: '骨盆后倾（平背）',
    nameEn: 'Posterior Pelvic Tilt',
    shortDesc: '腰椎曲度变平，屁股塌',
    icon: '🔁',
    views: ['side'],
    compensatingMuscles: [
      { name: '腘绳肌', reason: '代偿把骨盆拉向后倾' },
      { name: '腹直肌', reason: '过度紧张把骨盆向上拉' }
    ],
    shouldBeStrong: [
      { name: '髂腰肌', role: '让骨盆回到中立位' },
      { name: '臀大肌（离心控制）', role: '控制骨盆不下榻' },
      { name: '竖脊肌（腰部）', role: '维持腰椎自然曲度' }
    ],
    visualSignals: [
      '从侧面看：腰椎曲度变平',
      '屁股看起来扁平',
      '驼背倾向'
    ],
    detection: {
      pelvic_tilt_angle: { threshold: -10, weight: 1.0 }
    }
  },

  // ============================================================
  // 4. 膝内扣（膝外翻）
  // ============================================================
  knee_valgus: {
    id: 'knee_valgus',
    name: '膝内扣（膝外翻）',
    nameEn: 'Knee Valgus (Knock Knees)',
    shortDesc: '站立时膝盖向内靠',
    icon: '🦵',
    views: ['front', 'back'],
    compensatingMuscles: [
      { name: '髂胫束', reason: '代偿髋外侧稳定' },
      { name: '内收肌群', reason: '代偿大腿内收' },
      { name: '腓骨长肌', reason: '代偿足弓支撑' }
    ],
    shouldBeStrong: [
      { name: '臀中肌', role: '这是关键！维持髋外展和膝盖对齐' },
      { name: '臀大肌', role: '髋外旋，让膝盖不被拉向内' },
      { name: '股内侧斜肌（VMO）', role: '膝盖最后 30° 伸展，稳定髌骨' },
      { name: '胫骨后肌', role: '维持足弓，减少连锁反应' }
    ],
    visualSignals: [
      '从正面/背面看：两个膝盖向中线靠拢',
      '从下蹲看：膝盖向内塌',
      '伴随足外翻（扁平足倾向）'
    ],
    detection: {
      // 髋-膝-踝的水平偏移
      knee_inward_offset: { threshold: 0.05, weight: 0.7 },
      // 左右膝连线相对于髋-踝连线的内移
      knee_collapse_ratio: { threshold: 0.08, weight: 0.3 }
    }
  },

  // ============================================================
  // 5. 膝过伸
  // ============================================================
  knee_hyperextension: {
    id: 'knee_hyperextension',
    name: '膝过伸',
    nameEn: 'Knee Hyperextension',
    shortDesc: '站立时膝盖向后顶',
    icon: '📐',
    views: ['side'],
    compensatingMuscles: [
      { name: '腓肠肌', reason: '代偿稳定' },
      { name: '股四头肌（紧张）', reason: '代偿锁定膝盖' }
    ],
    shouldBeStrong: [
      { name: '腘绳肌', role: '对抗膝过伸，提供动态稳定' },
      { name: '股内侧斜肌（VMO）', role: '主动稳定膝盖' },
      { name: '比目鱼肌', role: '深层踝稳定' }
    ],
    visualSignals: [
      '从侧面看：膝盖中心在髋-踝连线的后方',
      '站立时膝盖绷得很直，呈反向弧度'
    ],
    detection: {
      knee_angle: { threshold: 185, weight: 1.0 } // 度
    }
  },

  // ============================================================
  // 6. 高低肩
  // ============================================================
  shoulder_asymmetry: {
    id: 'shoulder_asymmetry',
    name: '高低肩',
    nameEn: 'Uneven Shoulders',
    shortDesc: '左右肩膀高度不一致',
    icon: '↕️',
    views: ['front', 'back'],
    compensatingMuscles: [
      { name: '高侧上斜方肌', reason: '代偿性紧张' },
      { name: '高侧肩胛提肌', reason: '代偿性紧张' }
    ],
    shouldBeStrong: [
      { name: '低侧前锯肌', role: '稳定肩胛，平衡' },
      { name: '低侧中下斜方肌', role: '把肩胛向下拉' },
      { name: '低侧腹斜肌', role: '躯干侧链稳定' },
      { name: '高侧腰方肌（被拉长）', role: '需要激活离心控制' }
    ],
    visualSignals: [
      '从正面/背面看：两肩高度明显不一致',
      '常见伴随骨盆高低（连锁反应）'
    ],
    detection: {
      shoulder_height_diff: { threshold: 0.03, weight: 0.7 }, // 归一化差
      hip_height_diff: { threshold: 0.03, weight: 0.3 }
    }
  },

  // ============================================================
  // 7. 翼状肩胛
  // ============================================================
  winged_scapula: {
    id: 'winged_scapula',
    name: '翼状肩胛',
    nameEn: 'Winged Scapula',
    shortDesc: '肩胛骨内侧缘突出',
    icon: '🦋',
    views: ['back'],
    compensatingMuscles: [
      { name: '菱形肌（被拉长）', reason: '试图把肩胛拉回但力量不够' },
      { name: '中下斜方肌（被拉长）', reason: '同上' }
    ],
    shouldBeStrong: [
      { name: '前锯肌', role: '这是最关键的！把肩胛贴紧胸廓' },
      { name: '下斜方肌', role: '向上回旋 + 后倾肩胛' },
      { name: '胸长神经支配的肌肉', role: '需要神经控制恢复' }
    ],
    visualSignals: [
      '从背面看：肩胛骨内侧缘明显突出',
      '推墙测试时肩胛骨会"飞起来"'
    ],
    detection: {
      // 这个在静态照片中较难，需要从肩胛骨位置估算
      // 简化：用肩峰和脊柱中线的距离差异做粗略判断
      scapular_prominence: { threshold: 0.10, weight: 1.0 }
    }
  },

  // ============================================================
  // 8. 头侧倾 / 颈椎侧弯倾向
  // ============================================================
  head_lateral_tilt: {
    id: 'head_lateral_tilt',
    name: '头部侧倾',
    nameEn: 'Lateral Head Tilt',
    shortDesc: '头向一侧倾斜',
    icon: '🤷',
    views: ['front', 'back'],
    compensatingMuscles: [
      { name: '高侧上斜方肌', reason: '缩短紧张' },
      { name: '高侧胸锁乳突肌', reason: '缩短紧张' }
    ],
    shouldBeStrong: [
      { name: '低侧颈深屈肌', role: '保持颈椎中立' },
      { name: '低侧肩胛提肌（被拉长）', role: '需要激活离心控制' }
    ],
    visualSignals: [
      '从正面看：两耳高度不一致',
      '从背面看：头偏离身体中线'
    ],
    detection: {
      head_tilt_angle: { threshold: 5, weight: 1.0 } // 度
    }
  }
};

// ============================================================
// 训练动作库 —— 按"应发力肌"分组
// 每条动作包含：针对肌肉、动作名、要点、组数次数、难度、示范
// ============================================================
const EXERCISES = {
  // 颈深屈肌
  deepNeckFlexors: [
    {
      id: 'chin_tuck',
      name: '收下巴（Chin Tuck）',
      target: '颈深屈肌',
      level: 1,
      sets: 3,
      reps: '12 次',
      duration: '每个保持 3 秒',
      cues: [
        '背靠墙站立，后脑勺轻触墙面',
        '想象下颌往脖子方向"画一个点"',
        '不要低头，只是水平向后收',
        '感受脖子前侧深层轻轻发力',
        '呼气时收，吸气时缓慢回到中立'
      ],
      avoid: [
        '不要低头或抬下巴',
        '不要用下巴去找胸口'
      ],
      svg: 'chinTuck'
    },
    {
      id: 'supine_head_lift',
      name: '仰卧抬头（Supine Head Lift）',
      target: '颈深屈肌',
      level: 2,
      sets: 3,
      reps: '10 次',
      duration: '每个保持 5 秒',
      cues: [
        '仰卧，膝盖弯曲，双脚平放',
        '下巴保持内收（想象下巴里夹着一个网球）',
        '头部离地 2-3 厘米，只需要一点点',
        '保持 5 秒，缓慢放下'
      ],
      avoid: [
        '不要抬得太高',
        '不要憋气'
      ],
      svg: 'headLift'
    }
  ],

  // 下斜方肌 + 前锯肌
  lowerTrap_serratus: [
    {
      id: 'prone_y_raise',
      name: '俯卧 Y 形抬臂',
      target: '下斜方肌',
      level: 1,
      sets: 3,
      reps: '10 次',
      duration: '抬起保持 2 秒',
      cues: [
        '俯卧在垫子上，手臂伸直呈 Y 字形（大拇指朝上）',
        '下巴微收，后脑勺远离脖子',
        '保持肩胛骨向下沉的感觉',
        '抬起手臂到与肩同高，拇指指向天花板',
        '感受肩胛骨向下滑动'
      ],
      avoid: [
        '不要耸肩',
        '不要过度抬头（不要挤压颈椎）'
      ],
      svg: 'proneY'
    },
    {
      id: 'wall_slide',
      name: '靠墙滑臂（Wall Slide）',
      target: '下斜方肌 + 前锯肌',
      level: 2,
      sets: 3,
      reps: '10 次',
      duration: '全程 3 秒',
      cues: [
        '背贴墙站立，后脑勺、肩胛骨、臀部贴墙',
        '双手呈 W 形贴墙（手背、肘、肩尽量贴墙）',
        '沿墙向上滑动手臂到头顶，再缓慢滑下',
        '全程保持手臂贴墙'
      ],
      avoid: [
        '如果手臂贴不到墙，可以屈肘一点',
        '腰部不要过度弓起'
      ],
      svg: 'wallSlide'
    },
    {
      id: 'scapular_pushup',
      name: '肩胛俯卧撑（Scapular Push-up）',
      target: '前锯肌',
      level: 1,
      sets: 3,
      reps: '12 次',
      duration: '挤压保持 2 秒',
      cues: [
        '俯撑姿势，手臂伸直',
        '保持手臂伸直，只让肩胛骨向中线挤再打开',
        '幅度很小，但全程感受前锯肌发力',
        '感觉像是把地板往两边推开'
      ],
      avoid: [
        '不要弯肘',
        '不要塌腰'
      ],
      svg: 'scapPushup'
    }
  ],

  // 臀大肌
  gluteMax: [
    {
      id: 'glute_bridge',
      name: '臀桥（Glute Bridge）',
      target: '臀大肌 + 腘绳肌',
      level: 1,
      sets: 3,
      reps: '12 次',
      duration: '顶部保持 3 秒',
      cues: [
        '仰卧，膝盖弯曲，双脚与髋同宽',
        '脚跟发力踩地，臀部向上抬',
        '在顶部夹紧臀部 3 秒',
        '膝盖、髋、肩成一条直线',
        '缓慢下落，不要塌腰'
      ],
      avoid: [
        '不要在顶部过度挺腰（过度伸展腰椎）',
        '不要夹紧大腿内侧'
      ],
      svg: 'bridge'
    },
    {
      id: 'single_leg_bridge',
      name: '单腿臀桥',
      target: '臀大肌',
      level: 2,
      sets: 3,
      reps: '每侧 8 次',
      duration: '顶部保持 2 秒',
      cues: [
        '基础臀桥姿势，抬起一条腿伸直',
        '靠支撑腿发力把髋部顶起',
        '髋部保持水平，不要歪',
        '感受臀大肌挤压'
      ],
      avoid: [
        '髋部不要向一侧掉',
        '如果腰酸说明臀没发力'
      ],
      svg: 'singleLegBridge'
    }
  ],

  // 臀中肌
  gluteMed: [
    {
      id: 'clamshell',
      name: '贝壳（Clamshell）',
      target: '臀中肌',
      level: 1,
      sets: 3,
      reps: '每侧 15 次',
      duration: '顶部保持 2 秒',
      cues: [
        '侧卧，膝盖弯曲 90°，髋部叠在一起',
        '双脚并拢，膝盖像贝壳一样向上打开',
        '感受臀部外侧发力',
        '髋部不要向后倒'
      ],
      avoid: [
        '不要让髋部向后倒',
        '不要追求高度，幅度不重要'
      ],
      svg: 'clamshell'
    },
    {
      id: 'side_leg_raise',
      name: '侧卧抬腿',
      target: '臀中肌',
      level: 1,
      sets: 3,
      reps: '每侧 12 次',
      duration: '缓慢控制',
      cues: [
        '侧卧，下侧手臂撑头，上侧手扶地',
        '腿伸直，脚尖朝前',
        '整条腿向上抬 30-45°',
        '缓慢下落（3 秒离心）'
      ],
      avoid: [
        '不要向外旋腿（脚尖朝天花板）',
        '不要快上快下'
      ],
      svg: 'sideLegRaise'
    }
  ],

  // 核心（腹横肌 + 多裂肌）
  core: [
    {
      id: 'dead_bug',
      name: '死虫式（Dead Bug）',
      target: '腹横肌 + 多裂肌',
      level: 2,
      sets: 3,
      reps: '每侧 8 次',
      duration: '全程保持腹压',
      cues: [
        '仰卧，双手指向天花板，膝盖弯曲 90°',
        '腰部贴地（想象腰下面夹一支笔）',
        '对侧手脚同时缓慢伸出（右手 + 左脚）',
        '保持腰部贴地（这是关键）',
        '吸气准备，呼气时伸展'
      ],
      avoid: [
        '腰部如果离开地面就降阶（只动手）',
        '不要憋气'
      ],
      svg: 'deadBug'
    },
    {
      id: 'bird_dog',
      name: '鸟狗式（Bird Dog）',
      target: '腹横肌 + 多裂肌 + 臀大肌',
      level: 2,
      sets: 3,
      reps: '每侧 8 次',
      duration: '每个保持 3 秒',
      cues: [
        '四点跪姿，腕在肩下，膝在髋下',
        '对侧手脚同时伸出（右手 + 左脚）',
        '保持脊柱中立（不要塌腰、不要弓背）',
        '伸出的手和脚与地面平行',
        '保持 3 秒后缓慢收回'
      ],
      avoid: [
        '不要塌腰',
        '不要歪斜（想象背上放一杯水）'
      ],
      svg: 'birdDog'
    },
    {
      id: '90_90_breath',
      name: '90/90 呼吸',
      target: '腹横肌 + 膈肌',
      level: 1,
      sets: 1,
      reps: '20 次呼吸',
      duration: '每天必做',
      cues: [
        '仰卧，膝盖弯曲 90°，小腿与地面平行',
        '一只手放胸口，一只手放肚子',
        '吸气时让肚子鼓起（360°鼓起），胸口不要动',
        '呼气时肚子收回，想象肚脐找脊椎',
        '整个过程保持腰部贴地'
      ],
      avoid: [
        '不要用胸口呼吸',
        '不要憋气'
      ],
      svg: 'breathing'
    }
  ],

  // 腘绳肌
  hamstrings: [
    {
      id: 'glute_bridge_curl',
      name: '臀桥腿弯举（需要弹力带或瑞士球）',
      target: '腘绳肌 + 臀大肌',
      level: 2,
      sets: 3,
      reps: '10 次',
      duration: '顶部保持 2 秒',
      cues: [
        '基础臀桥姿势，双脚踩在瑞士球上（或弹力带绕圈）',
        '髋部抬到顶，夹紧臀大肌',
        '双脚把球向远处滚动（伸膝）',
        '再拉回来，全程保持髋部高度'
      ],
      avoid: [
        '髋部不要下沉'
      ],
      svg: 'bridgeCurl'
    }
  ],

  // VMO
  vmo: [
    {
      id: 'terminal_knee_extension',
      name: '末端伸膝（Terminal Knee Extension）',
      target: '股内侧斜肌 (VMO)',
      level: 1,
      sets: 3,
      reps: '每侧 15 次',
      duration: '顶部保持 2 秒',
      cues: [
        '靠墙半蹲姿势，膝盖与弹力带之间',
        '弹力带另一端固定在膝盖外侧',
        '把膝盖往内推（对抗弹力带），让膝盖对准第二脚趾',
        '缓慢放回，全程控制'
      ],
      avoid: [
        '膝盖不要内扣',
        '保持膝盖对准脚尖方向'
      ],
      svg: 'tke'
    }
  ],

  // 胫骨后肌 / 足弓
  footIntrinsics: [
    {
      id: 'short_foot',
      name: '短足训练（Short Foot）',
      target: '足底内在肌 + 胫骨后肌',
      level: 1,
      sets: 3,
      reps: '每侧 12 次',
      duration: '保持 5 秒',
      cues: [
        '站立或坐位，脚平放地面',
        '足弓向上提（脚趾不要抓地）',
        '感觉大脚趾根部和脚跟相互靠近',
        '保持 5 秒，慢慢放下'
      ],
      avoid: [
        '脚趾不要抓地',
        '不要让脚外侧翻起来'
      ],
      svg: 'shortFoot'
    }
  ]
};

// ============================================================
// 关键映射：体态问题 → 推荐动作
// 这是"治疗处方"的核心
// ============================================================
const TREATMENT_MAP = {
  fhp_rounded_shoulder: {
    primary: ['deepNeckFlexors', 'lowerTrap_serratus'],
    secondary: ['core'],
    description: '头前伸圆肩的核心是：颈深屈肌和下斜方肌被抑制，胸和上斜方肌代偿。治疗重点是激活颈深 + 拉回肩胛。'
  },
  anterior_pelvic_tilt: {
    primary: ['gluteMax', 'core'],
    secondary: ['hamstrings'],
    description: '骨盆前倾的根源是：臀大肌和腹横肌被抑制，髂腰肌代偿把骨盆拉向前。臀大肌和核心是处方核心。'
  },
  posterior_pelvic_tilt: {
    primary: ['core', 'gluteMax'],
    secondary: ['deepNeckFlexors'],
    description: '骨盆后倾的根源是：髂腰肌（屈髋）被抑制，腘绳肌和腹直肌代偿。需要恢复髋屈曲主动控制。'
  },
  knee_valgus: {
    primary: ['gluteMed', 'gluteMax'],
    secondary: ['vmo', 'footIntrinsics'],
    description: '膝内扣的根源是：臀中肌和臀大肌被抑制，髂胫束和内收肌代偿。臀中肌是处方核心。'
  },
  knee_hyperextension: {
    primary: ['vmo', 'hamstrings'],
    secondary: ['gluteMax'],
    description: '膝过伸意味着腘绳肌和 VMO 不能主动稳定膝关节。需要重新建立膝周肌群的动态控制。'
  },
  shoulder_asymmetry: {
    primary: ['lowerTrap_serratus', 'core'],
    secondary: ['gluteMed'],
    description: '高低肩往往伴随骨盆高低（连锁反应）。需要平衡两侧肩胛 + 核心侧链。'
  },
  winged_scapula: {
    primary: ['lowerTrap_serratus'],
    secondary: ['core'],
    description: '翼状肩胛的核心是前锯肌被抑制（常伴有胸长神经问题）。前锯肌激活 + 下斜方肌是处方。'
  },
  head_lateral_tilt: {
    primary: ['deepNeckFlexors', 'lowerTrap_serratus'],
    secondary: ['core'],
    description: '头部侧倾的根源是：低侧颈深屈肌被拉长抑制。需要双侧颈深 + 肩胛稳定。'
  }
};

// 导出
window.POSTURE_PATTERNS = POSTURE_PATTERNS;
window.EXERCISES = EXERCISES;
window.TREATMENT_MAP = TREATMENT_MAP;
