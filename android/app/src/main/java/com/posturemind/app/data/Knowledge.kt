package com.posturemind.app.data

/**
 * 知识库 —— 8 种体态问题 + 14 个训练动作 + 治疗映射
 *
 * 这是整个 APP 的"灵魂"。
 * 编辑这里 = 改变整个产品的诊断逻辑。
 */
object Knowledge {

    // ============================================================
    // 体态问题
    // ============================================================

    val FHP_ROUNDED_SHOULDER = PosturePattern(
        id = "fhp_rounded_shoulder",
        name = "头前伸 + 圆肩",
        nameEn = "Forward Head & Rounded Shoulders",
        shortDesc = "耳朵在肩膀前方，肩膀在胸前",
        icon = "🔄",
        views = listOf("side", "front"),
        compensatingMuscles = listOf(
            Muscle("上斜方肌", reason = "代偿抬头，肩颈僵硬"),
            Muscle("胸锁乳突肌", reason = "代偿屈颈，脖子前面紧"),
            Muscle("胸大肌 / 胸小肌", reason = "代偿肩内旋，胸前紧"),
            Muscle("肩胛提肌", reason = "代偿提肩")
        ),
        shouldBeStrong = listOf(
            Muscle("颈深屈肌（颈长肌、头长肌）", role = "稳定颈椎，让头回到肩膀正上方"),
            Muscle("下斜方肌", role = "把肩胛骨向下拉，对抗耸肩"),
            Muscle("前锯肌", role = "把肩胛骨贴紧胸廓，对抗圆肩"),
            Muscle("中斜方肌 + 菱形肌", role = "把肩胛骨向脊柱拉，对抗前伸"),
            Muscle("肩外旋肌（冈下肌、小圆肌）", role = "让肱骨头回到肩窝中心")
        ),
        visualSignals = listOf(
            "从侧面看：耳朵在肩膀垂线的前方",
            "从侧面看：肩峰在髋部垂线的前方",
            "从正面看：肩膀高度不一致 / 锁骨外侧上翘",
            "脖子后面有\"富贵包\"或皮肤褶皱"
        )
    )

    val ANTERIOR_PELVIC_TILT = PosturePattern(
        id = "anterior_pelvic_tilt",
        name = "骨盆前倾（腰椎过伸）",
        nameEn = "Anterior Pelvic Tilt",
        shortDesc = "下背部弧度过大，小肚子突出",
        icon = "🔃",
        views = listOf("side"),
        compensatingMuscles = listOf(
            Muscle("髂腰肌", reason = "代偿屈髋，让骨盆被拉向前倾"),
            Muscle("股直肌", reason = "代偿屈髋 + 伸膝"),
            Muscle("竖脊肌（腰部）", reason = "代偿稳定腰椎，过度紧张")
        ),
        shouldBeStrong = listOf(
            Muscle("臀大肌", role = "骨盆后倾的原动力 —— 这是关键"),
            Muscle("腘绳肌", role = "辅助骨盆后倾"),
            Muscle("腹横肌", role = "腹内压核心，让骨盆不被拉前"),
            Muscle("多裂肌", role = "深层稳定腰椎"),
            Muscle("臀中肌", role = "稳定骨盆水平面")
        ),
        visualSignals = listOf(
            "从侧面看：髋部前缘比髋部后缘更靠前",
            "从侧面看：腰椎弧度明显（腰窝很深）",
            "小肚子突出（不是胖，是骨盆前倾顶出来的）",
            "屁股看起来很翘（但不是真的翘，是前倾造成的视觉）"
        )
    )

    val POSTERIOR_PELVIC_TILT = PosturePattern(
        id = "posterior_pelvic_tilt",
        name = "骨盆后倾（平背）",
        nameEn = "Posterior Pelvic Tilt",
        shortDesc = "腰椎曲度变平，屁股塌",
        icon = "🔁",
        views = listOf("side"),
        compensatingMuscles = listOf(
            Muscle("腘绳肌", reason = "代偿把骨盆拉向后倾"),
            Muscle("腹直肌", reason = "过度紧张把骨盆向上拉")
        ),
        shouldBeStrong = listOf(
            Muscle("髂腰肌", role = "让骨盆回到中立位"),
            Muscle("臀大肌（离心控制）", role = "控制骨盆不下榻"),
            Muscle("竖脊肌（腰部）", role = "维持腰椎自然曲度")
        ),
        visualSignals = listOf(
            "从侧面看：腰椎曲度变平",
            "屁股看起来扁平",
            "驼背倾向"
        )
    )

    val KNEE_VALGUS = PosturePattern(
        id = "knee_valgus",
        name = "膝内扣（膝外翻）",
        nameEn = "Knee Valgus (Knock Knees)",
        shortDesc = "站立时膝盖向内靠",
        icon = "🦵",
        views = listOf("front", "back"),
        compensatingMuscles = listOf(
            Muscle("髂胫束", reason = "代偿髋外侧稳定"),
            Muscle("内收肌群", reason = "代偿大腿内收"),
            Muscle("腓骨长肌", reason = "代偿足弓支撑")
        ),
        shouldBeStrong = listOf(
            Muscle("臀中肌", role = "这是关键！维持髋外展和膝盖对齐"),
            Muscle("臀大肌", role = "髋外旋，让膝盖不被拉向内"),
            Muscle("股内侧斜肌（VMO）", role = "膝盖最后 30° 伸展，稳定髌骨"),
            Muscle("胫骨后肌", role = "维持足弓，减少连锁反应")
        ),
        visualSignals = listOf(
            "从正面/背面看：两个膝盖向中线靠拢",
            "从下蹲看：膝盖向内塌",
            "伴随足外翻（扁平足倾向）"
        )
    )

    val KNEE_HYPEREXTENSION = PosturePattern(
        id = "knee_hyperextension",
        name = "膝过伸",
        nameEn = "Knee Hyperextension",
        shortDesc = "站立时膝盖向后顶",
        icon = "📐",
        views = listOf("side"),
        compensatingMuscles = listOf(
            Muscle("腓肠肌", reason = "代偿稳定"),
            Muscle("股四头肌（紧张）", reason = "代偿锁定膝盖")
        ),
        shouldBeStrong = listOf(
            Muscle("腘绳肌", role = "对抗膝过伸，提供动态稳定"),
            Muscle("股内侧斜肌（VMO）", role = "主动稳定膝盖"),
            Muscle("比目鱼肌", role = "深层踝稳定")
        ),
        visualSignals = listOf(
            "从侧面看：膝盖中心在髋-踝连线的后方",
            "站立时膝盖绷得很直，呈反向弧度"
        )
    )

    val SHOULDER_ASYMMETRY = PosturePattern(
        id = "shoulder_asymmetry",
        name = "高低肩",
        nameEn = "Uneven Shoulders",
        shortDesc = "左右肩膀高度不一致",
        icon = "↕️",
        views = listOf("front", "back"),
        compensatingMuscles = listOf(
            Muscle("高侧上斜方肌", reason = "代偿性紧张"),
            Muscle("高侧肩胛提肌", reason = "代偿性紧张")
        ),
        shouldBeStrong = listOf(
            Muscle("低侧前锯肌", role = "稳定肩胛，平衡"),
            Muscle("低侧中下斜方肌", role = "把肩胛向下拉"),
            Muscle("低侧腹斜肌", role = "躯干侧链稳定"),
            Muscle("高侧腰方肌（被拉长）", role = "需要激活离心控制")
        ),
        visualSignals = listOf(
            "从正面/背面看：两肩高度明显不一致",
            "常见伴随骨盆高低（连锁反应）"
        )
    )

    val WINGED_SCAPULA = PosturePattern(
        id = "winged_scapula",
        name = "翼状肩胛",
        nameEn = "Winged Scapula",
        shortDesc = "肩胛骨内侧缘突出",
        icon = "🦋",
        views = listOf("back"),
        compensatingMuscles = listOf(
            Muscle("菱形肌（被拉长）", reason = "试图把肩胛拉回但力量不够"),
            Muscle("中下斜方肌（被拉长）", reason = "同上")
        ),
        shouldBeStrong = listOf(
            Muscle("前锯肌", role = "这是最关键的！把肩胛贴紧胸廓"),
            Muscle("下斜方肌", role = "向上回旋 + 后倾肩胛"),
            Muscle("胸长神经支配的肌肉", role = "需要神经控制恢复")
        ),
        visualSignals = listOf(
            "从背面看：肩胛骨内侧缘明显突出",
            "推墙测试时肩胛骨会\"飞起来\""
        )
    )

    val HEAD_LATERAL_TILT = PosturePattern(
        id = "head_lateral_tilt",
        name = "头部侧倾",
        nameEn = "Lateral Head Tilt",
        shortDesc = "头向一侧倾斜",
        icon = "🤷",
        views = listOf("front", "back"),
        compensatingMuscles = listOf(
            Muscle("高侧上斜方肌", reason = "缩短紧张"),
            Muscle("高侧胸锁乳突肌", reason = "缩短紧张")
        ),
        shouldBeStrong = listOf(
            Muscle("低侧颈深屈肌", role = "保持颈椎中立"),
            Muscle("低侧肩胛提肌（被拉长）", role = "需要激活离心控制")
        ),
        visualSignals = listOf(
            "从正面看：两耳高度不一致",
            "从背面看：头偏离身体中线"
        )
    )

    val ALL_PATTERNS: Map<String, PosturePattern> = listOf(
        FHP_ROUNDED_SHOULDER,
        ANTERIOR_PELVIC_TILT,
        POSTERIOR_PELVIC_TILT,
        KNEE_VALGUS,
        KNEE_HYPEREXTENSION,
        SHOULDER_ASYMMETRY,
        WINGED_SCAPULA,
        HEAD_LATERAL_TILT
    ).associateBy { it.id }

    // ============================================================
    // 训练动作
    // ============================================================

    val EXERCISES: Map<String, List<Exercise>> = mapOf(
        "deepNeckFlexors" to listOf(
            Exercise(
                id = "chin_tuck",
                name = "收下巴（Chin Tuck）",
                target = "颈深屈肌",
                level = 1,
                sets = 3,
                reps = "12 次",
                duration = "每个保持 3 秒",
                cues = listOf(
                    "背靠墙站立，后脑勺轻触墙面",
                    "想象下颌往脖子方向\"画一个点\"",
                    "不要低头，只是水平向后收",
                    "感受脖子前侧深层轻轻发力",
                    "呼气时收，吸气时缓慢回到中立"
                ),
                avoid = listOf(
                    "不要低头或抬下巴",
                    "不要用下巴去找胸口"
                ),
                svgKey = "chinTuck"
            ),
            Exercise(
                id = "supine_head_lift",
                name = "仰卧抬头（Supine Head Lift）",
                target = "颈深屈肌",
                level = 2,
                sets = 3,
                reps = "10 次",
                duration = "每个保持 5 秒",
                cues = listOf(
                    "仰卧，膝盖弯曲，双脚平放",
                    "下巴保持内收（想象下巴里夹着一个网球）",
                    "头部离地 2-3 厘米，只需要一点点",
                    "保持 5 秒，缓慢放下"
                ),
                avoid = listOf(
                    "不要抬得太高",
                    "不要憋气"
                ),
                svgKey = "headLift"
            )
        ),
        "lowerTrap_serratus" to listOf(
            Exercise(
                id = "prone_y_raise",
                name = "俯卧 Y 形抬臂",
                target = "下斜方肌",
                level = 1,
                sets = 3,
                reps = "10 次",
                duration = "抬起保持 2 秒",
                cues = listOf(
                    "俯卧在垫子上，手臂伸直呈 Y 字形（大拇指朝上）",
                    "下巴微收，后脑勺远离脖子",
                    "保持肩胛骨向下沉的感觉",
                    "抬起手臂到与肩同高，拇指指向天花板",
                    "感受肩胛骨向下滑动"
                ),
                avoid = listOf(
                    "不要耸肩",
                    "不要过度抬头（不要挤压颈椎）"
                ),
                svgKey = "proneY"
            ),
            Exercise(
                id = "wall_slide",
                name = "靠墙滑臂（Wall Slide）",
                target = "下斜方肌 + 前锯肌",
                level = 2,
                sets = 3,
                reps = "10 次",
                duration = "全程 3 秒",
                cues = listOf(
                    "背贴墙站立，后脑勺、肩胛骨、臀部贴墙",
                    "双手呈 W 形贴墙（手背、肘、肩尽量贴墙）",
                    "沿墙向上滑动手臂到头顶，再缓慢滑下",
                    "全程保持手臂贴墙"
                ),
                avoid = listOf(
                    "如果手臂贴不到墙，可以屈肘一点",
                    "腰部不要过度弓起"
                ),
                svgKey = "wallSlide"
            ),
            Exercise(
                id = "scap_pushup",
                name = "肩胛俯卧撑（Scapular Push-up）",
                target = "前锯肌",
                level = 1,
                sets = 3,
                reps = "12 次",
                duration = "挤压保持 2 秒",
                cues = listOf(
                    "俯撑姿势，手臂伸直",
                    "保持手臂伸直，只让肩胛骨向中线挤再打开",
                    "幅度很小，但全程感受前锯肌发力",
                    "感觉像是把地板往两边推开"
                ),
                avoid = listOf(
                    "不要弯肘",
                    "不要塌腰"
                ),
                svgKey = "scapPushup"
            )
        ),
        "gluteMax" to listOf(
            Exercise(
                id = "glute_bridge",
                name = "臀桥（Glute Bridge）",
                target = "臀大肌 + 腘绳肌",
                level = 1,
                sets = 3,
                reps = "12 次",
                duration = "顶部保持 3 秒",
                cues = listOf(
                    "仰卧，膝盖弯曲，双脚与髋同宽",
                    "脚跟发力踩地，臀部向上抬",
                    "在顶部夹紧臀部 3 秒",
                    "膝盖、髋、肩成一条直线",
                    "缓慢下落，不要塌腰"
                ),
                avoid = listOf(
                    "不要在顶部过度挺腰（过度伸展腰椎）",
                    "不要夹紧大腿内侧"
                ),
                svgKey = "bridge"
            ),
            Exercise(
                id = "single_leg_bridge",
                name = "单腿臀桥",
                target = "臀大肌",
                level = 2,
                sets = 3,
                reps = "每侧 8 次",
                duration = "顶部保持 2 秒",
                cues = listOf(
                    "基础臀桥姿势，抬起一条腿伸直",
                    "靠支撑腿发力把髋部顶起",
                    "髋部保持水平，不要歪",
                    "感受臀大肌挤压"
                ),
                avoid = listOf(
                    "髋部不要向一侧掉",
                    "如果腰酸说明臀没发力"
                ),
                svgKey = "singleLegBridge"
            )
        ),
        "gluteMed" to listOf(
            Exercise(
                id = "clamshell",
                name = "贝壳（Clamshell）",
                target = "臀中肌",
                level = 1,
                sets = 3,
                reps = "每侧 15 次",
                duration = "顶部保持 2 秒",
                cues = listOf(
                    "侧卧，膝盖弯曲 90°，髋部叠在一起",
                    "双脚并拢，膝盖像贝壳一样向上打开",
                    "感受臀部外侧发力",
                    "髋部不要向后倒"
                ),
                avoid = listOf(
                    "不要让髋部向后倒",
                    "不要追求高度，幅度不重要"
                ),
                svgKey = "clamshell"
            ),
            Exercise(
                id = "side_leg_raise",
                name = "侧卧抬腿",
                target = "臀中肌",
                level = 1,
                sets = 3,
                reps = "每侧 12 次",
                duration = "缓慢控制",
                cues = listOf(
                    "侧卧，下侧手臂撑头，上侧手扶地",
                    "腿伸直，脚尖朝前",
                    "整条腿向上抬 30-45°",
                    "缓慢下落（3 秒离心）"
                ),
                avoid = listOf(
                    "不要向外旋腿（脚尖朝天花板）",
                    "不要快上快下"
                ),
                svgKey = "sideLegRaise"
            )
        ),
        "core" to listOf(
            Exercise(
                id = "dead_bug",
                name = "死虫式（Dead Bug）",
                target = "腹横肌 + 多裂肌",
                level = 2,
                sets = 3,
                reps = "每侧 8 次",
                duration = "全程保持腹压",
                cues = listOf(
                    "仰卧，双手指向天花板，膝盖弯曲 90°",
                    "腰部贴地（想象腰下面夹一支笔）",
                    "对侧手脚同时缓慢伸出（右手 + 左脚）",
                    "保持腰部贴地（这是关键）",
                    "吸气准备，呼气时伸展"
                ),
                avoid = listOf(
                    "腰部如果离开地面就降阶（只动手）",
                    "不要憋气"
                ),
                svgKey = "deadBug"
            ),
            Exercise(
                id = "bird_dog",
                name = "鸟狗式（Bird Dog）",
                target = "腹横肌 + 多裂肌 + 臀大肌",
                level = 2,
                sets = 3,
                reps = "每侧 8 次",
                duration = "每个保持 3 秒",
                cues = listOf(
                    "四点跪姿，腕在肩下，膝在髋下",
                    "对侧手脚同时伸出（右手 + 左脚）",
                    "保持脊柱中立（不要塌腰、不要弓背）",
                    "伸出的手和脚与地面平行",
                    "保持 3 秒后缓慢收回"
                ),
                avoid = listOf(
                    "不要塌腰",
                    "不要歪斜（想象背上放一杯水）"
                ),
                svgKey = "birdDog"
            ),
            Exercise(
                id = "breath_90_90",
                name = "90/90 呼吸",
                target = "腹横肌 + 膈肌",
                level = 1,
                sets = 1,
                reps = "20 次呼吸",
                duration = "每天必做",
                cues = listOf(
                    "仰卧，膝盖弯曲 90°，小腿与地面平行",
                    "一只手放胸口，一只手放肚子",
                    "吸气时让肚子鼓起（360°鼓起），胸口不要动",
                    "呼气时肚子收回，想象肚脐找脊椎",
                    "整个过程保持腰部贴地"
                ),
                avoid = listOf(
                    "不要用胸口呼吸",
                    "不要憋气"
                ),
                svgKey = "breathing"
            )
        ),
        "hamstrings" to listOf(
            Exercise(
                id = "bridge_curl",
                name = "臀桥腿弯举（瑞士球/弹力带）",
                target = "腘绳肌 + 臀大肌",
                level = 2,
                sets = 3,
                reps = "10 次",
                duration = "顶部保持 2 秒",
                cues = listOf(
                    "基础臀桥姿势，双脚踩在瑞士球上（或弹力带绕圈）",
                    "髋部抬到顶，夹紧臀大肌",
                    "双脚把球向远处滚动（伸膝）",
                    "再拉回来，全程保持髋部高度"
                ),
                avoid = listOf(
                    "髋部不要下沉"
                ),
                svgKey = "bridgeCurl"
            )
        ),
        "vmo" to listOf(
            Exercise(
                id = "tke",
                name = "末端伸膝（Terminal Knee Extension）",
                target = "股内侧斜肌 (VMO)",
                level = 1,
                sets = 3,
                reps = "每侧 15 次",
                duration = "顶部保持 2 秒",
                cues = listOf(
                    "靠墙半蹲姿势，膝盖与弹力带之间",
                    "弹力带另一端固定在膝盖外侧",
                    "把膝盖往内推（对抗弹力带），让膝盖对准第二脚趾",
                    "缓慢放回，全程控制"
                ),
                avoid = listOf(
                    "膝盖不要内扣",
                    "保持膝盖对准脚尖方向"
                ),
                svgKey = "tke"
            )
        ),
        "footIntrinsics" to listOf(
            Exercise(
                id = "short_foot",
                name = "短足训练（Short Foot）",
                target = "足底内在肌 + 胫骨后肌",
                level = 1,
                sets = 3,
                reps = "每侧 12 次",
                duration = "保持 5 秒",
                cues = listOf(
                    "站立或坐位，脚平放地面",
                    "足弓向上提（脚趾不要抓地）",
                    "感觉大脚趾根部和脚跟相互靠近",
                    "保持 5 秒，慢慢放下"
                ),
                avoid = listOf(
                    "脚趾不要抓地",
                    "不要让脚外侧翻起来"
                ),
                svgKey = "shortFoot"
            )
        )
    )

    // ============================================================
    // 治疗映射：体态问题 → 推荐动作
    // ============================================================

    val TREATMENT_MAP: Map<String, Treatment> = mapOf(
        "fhp_rounded_shoulder" to Treatment(
            primary = listOf("deepNeckFlexors", "lowerTrap_serratus"),
            secondary = listOf("core"),
            description = "头前伸圆肩的核心是：颈深屈肌和下斜方肌被抑制，胸和上斜方肌代偿。治疗重点是激活颈深 + 拉回肩胛。"
        ),
        "anterior_pelvic_tilt" to Treatment(
            primary = listOf("gluteMax", "core"),
            secondary = listOf("hamstrings"),
            description = "骨盆前倾的根源是：臀大肌和腹横肌被抑制，髂腰肌代偿把骨盆拉向前。臀大肌和核心是处方核心。"
        ),
        "posterior_pelvic_tilt" to Treatment(
            primary = listOf("core", "gluteMax"),
            secondary = listOf("deepNeckFlexors"),
            description = "骨盆后倾的根源是：髂腰肌（屈髋）被抑制，腘绳肌和腹直肌代偿。需要恢复髋屈曲主动控制。"
        ),
        "knee_valgus" to Treatment(
            primary = listOf("gluteMed", "gluteMax"),
            secondary = listOf("vmo", "footIntrinsics"),
            description = "膝内扣的根源是：臀中肌和臀大肌被抑制，髂胫束和内收肌代偿。臀中肌是处方核心。"
        ),
        "knee_hyperextension" to Treatment(
            primary = listOf("vmo", "hamstrings"),
            secondary = listOf("gluteMax"),
            description = "膝过伸意味着腘绳肌和 VMO 不能主动稳定膝关节。需要重新建立膝周肌群的动态控制。"
        ),
        "shoulder_asymmetry" to Treatment(
            primary = listOf("lowerTrap_serratus", "core"),
            secondary = listOf("gluteMed"),
            description = "高低肩往往伴随骨盆高低（连锁反应）。需要平衡两侧肩胛 + 核心侧链。"
        ),
        "winged_scapula" to Treatment(
            primary = listOf("lowerTrap_serratus"),
            secondary = listOf("core"),
            description = "翼状肩胛的核心是前锯肌被抑制（常伴有胸长神经问题）。前锯肌激活 + 下斜方肌是处方。"
        ),
        "head_lateral_tilt" to Treatment(
            primary = listOf("deepNeckFlexors", "lowerTrap_serratus"),
            secondary = listOf("core"),
            description = "头部侧倾的根源是：低侧颈深屈肌被拉长抑制。需要双侧颈深 + 肩胛稳定。"
        )
    )

    /**
     * 根据检测到的问题生成训练计划
     */
    fun buildTrainingPlan(issues: List<PostureIssue>): List<Exercise> {
        val plan = mutableListOf<Exercise>()
        val usedMuscleGroups = mutableSetOf<String>()

        for (issue in issues) {
            val treatment = TREATMENT_MAP[issue.pattern.id] ?: continue
            for (group in treatment.primary + treatment.secondary) {
                if (usedMuscleGroups.contains(group)) continue
                usedMuscleGroups.add(group)
                val exs = EXERCISES[group] ?: continue
                for (ex in exs) {
                    if (plan.none { it.id == ex.id }) {
                        plan.add(ex)
                    }
                }
            }
        }

        // 每个肌肉组最多 1-2 个动作
        return plan.take(6)
    }

    /**
     * 查找动作 by id
     */
    fun findExercise(id: String): Exercise? {
        return EXERCISES.values.flatten().find { it.id == id }
    }

    /**
     * 获取所有训练动作（用于通用训练）
     */
    fun getGenericPlan(): List<Exercise> = listOf(
        EXERCISES["core"]!![0],  // dead bug
        EXERCISES["core"]!![1],  // bird dog
        EXERCISES["gluteMax"]!![0],  // glute bridge
        EXERCISES["lowerTrap_serratus"]!![2]  // scap pushup
    )
}

data class Treatment(
    val primary: List<String>,
    val secondary: List<String>,
    val description: String
)
