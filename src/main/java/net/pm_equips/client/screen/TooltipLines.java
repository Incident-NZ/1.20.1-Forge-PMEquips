package net.pm_equips.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class TooltipLines {

    private TooltipLines() {}

    /** 常に「LShiftで詳細」を出し、押下時だけ detail を追加 */
    public static void addShiftExpanded(List<Component> tooltip, List<Component> detail) {
        tooltip.add(Component.literal("LShiftで詳細を表示"));
        if (Screen.hasShiftDown()) {
            tooltip.addAll(detail);
        }
    }

    /** 基本行を足してから Shift 詳細 */
    public static void addWithShift(
            List<Component> tooltip,
            List<Component> basic,
            List<Component> detail
    ) {
        tooltip.addAll(basic);
        addShiftExpanded(tooltip, detail);
    }

    // ===== アイテムごとの詳細をここにまとめて定義 =====

    public static final List<Component> PENITENCE_ARMOR = List.of(
            Component.literal("被ダメージ時、HPを2回復する"),
            Component.literal("銃はアブノーマリティに対して効果はない。"),
            Component.literal("無意識から抽出したのがアブノーマリティであるのならば、それを逆抽出して兵器に変えることができないだろうか。"),
            Component.literal("観測者に応じてその結果も様々だった。冠は着用者の精神を守るだろう。"),
            Component.literal("彼らがあなたを攻撃すると棘による痛みを感じる。"),
            Component.literal("しかし、あなたが彼らに攻撃しても棘による痛みを感じる。"),
            Component.literal("公正さが欠如した者には適切には働かないので注意が必要である。")
    );

    public static final List<Component> SODA_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("触るとアルミニウム材質のような質感の防具。"),
            Component.literal("防具としては非常に軽い。"),
            Component.literal("長く装着すると色あせた潮の香りがするという、何人かの職員の証言がある。")
    );

    public static final List<Component> WINGBEAT_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("妖精たちの加護を受けた武器は薄い光で輝いている。"),
            Component.literal("翼のコンパクトな見た目とは異なり、E.G.O自体はずっしり重い。")
    );

    public static final List<Component> BATH_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("鋭く切断された皮と、血の染みが奇妙な模様を作り出している。"),
            Component.literal("防具自体に深い悲しみと苦悶が封じ込められており、並の精神攻撃には十分持ちこたえることが可能である。"),
            Component.literal("精神汚染度が高い職員が着用した場合、毎晩のように悲惨な夢を見るようになるかもしれない。")
    );

    public static final List<Component> BEAK_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("傷は無くつやつやしている。"),
            Component.literal("抽出初期の形は小さすぎて、一人の子供がどうにか着用できるほどだった。"),
            Component.literal("血に濡れており、その胸部は生きているかのように動く。")
    );

    public static final List<Component> LANTERN_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("皓々と光る発光体は暗闇を容易の中を照らすことができる"),
            Component.literal("また、闇の中で餌の役割を果たすのにも優れている。"),
            Component.literal("しかし、鎧から伸びる歯はとてもリアルで恐ろしい。")
    );

    public static final List<Component> MATCH_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("原型は燃え尽きた灰から抽出された。"),
            Component.literal("見た目は炭の塊だが、大変便利である。"),
            Component.literal("灰かぶりのデザインは世界の輝きに対する憎しみの炎を連想させる。"),
            Component.literal("しかし、火に耐性があるかどうかは誰も知らない。")
    );

    public static final List<Component> RED_EYE_ARMOR = List.of(
            Component.literal("特殊能力：HPが満タンの時、移動速度が50%上がる"),
            Component.literal("この兵器の固執と残酷さは敵でも友人でも慈悲を示すことはない。"),
            Component.literal("見た目は炭の塊だが、大変便利である。"),
            Component.literal("しかし、距離が長すぎると、蜘蛛が邪魔になることがある。"),
            Component.literal("その目は暗闇でもターゲットを追跡する。")
    );

    public static final List<Component> REGLET_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("人類の未来を変える可能性を秘めた秘密研究は地下で開始された。"),
            Component.literal("死刑囚には尊厳すら守る価値がなかったので拘束衣に身を縛られたまま、脳が腐り出ても、誰も悲しまなかった。"),
            Component.literal("今は拘束具が装備として使われているが、過去の憎しみや怒りはまだ残っている。"),
            Component.literal("エージェントが拘束具の圧迫で苦しんでいる場合、精神検査のために送りださなければならない。")
    );

    public static final List<Component> SOLITUDE_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("軟弱な内面を隠したいのなら、孤独を盾にするといい。"),
            Component.literal("全てを共有するつもりがないなら、そもそも何も開示する必要はない。"),
            Component.literal("この防具は、使用者の脆弱な心を保護する。")

    );

    public static final List<Component> SOMEWHERE_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("理解しようとせず、使用のみに留めるように。"),
            Component.literal("このE.G.Oの利用者は忘れられたものを見ることを許可されているようだ。"),
            Component.literal("しかし、すべてのエージェントが異なるものを見ているので、何を見せているのかを確かめる術はない。"),
            Component.literal("そのゲシュタルトはすべての抽出時に変化し、数々の試みの末最終的に固定化された。")
    );

    public static final List<Component> TODAY_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("様々な表情が布で埋められている。"),
            Component.literal("顔の表情を見せないことは、恥ずかしがりのようなものかもしれない。"),
            Component.literal("もう自分の気持ちを隠すことができないと感じたら、あなたの顔を隠そう。")
    );

    public static final List<Component> BEAR_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("忘れ去られた純真さのようなクマの詰め物ははみ出ている。"),
            Component.literal("それは可愛らしく見え、子供が贈り物で受け取ることがあるかもしれない。"),
            Component.literal("壊れやすく破れる可能性があるため、特別なケースが必要である。"),
            Component.literal("気の毒に思った一部のエージェント達はそれを修理するように要求してきたが、修理の結果が予想できなかったため、その提案は却下された。"),
            Component.literal("そのふわふわした外見に騙されないように。")
    );

    public static final List<Component> BLOOD_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("足を切断した後でさえ、信じられない信念で森へ行き、元の場所に戻ろうとする。"),
            Component.literal("美しいレースは、美しい笑顔の女の子を連想させる。"),
            Component.literal("悲劇を繰り返さないように注意しなければならない。"),
            Component.literal("おそらくいつか、血まみれの靴が素晴らしい舞踏場へ向かわせるだろう。")
    );

    public static final List<Component> GALAXY_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("あなたの手の小石は、輝き、揺れ、くすぐり、それは宇宙になる。"),
            Component.literal("小石の中に宇宙があります。子供が泣けば星が生まれます。あなたの宇宙に私はいますか？")
    );

    public static final List<Component> HARVEST_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("知恵に憧れた最後の遺産。"),
            Component.literal("長い鍬は、新しい畑の代わりに人間の脳を耕した。"),
            Component.literal("この鍬の為にどれだけ多くの聡明な命が失われたことだろうか？")
    );

    public static final List<Component> ICE_SHARD_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("雪の宮殿に滞在するためには、暖かい外套が必要だった。"),
            Component.literal("それは雪で作られた外套なので、一日で溶ける。"),
            Component.literal("雪が溶ける日が来たら、心も溶けるだろう")
    );

    public static final List<Component> LAETITIA_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("コートにつけられたリボンは、子供の無邪気さに対する憧れを象徴している。"),
            Component.literal("友達を置いてこれなかったちびは、素晴らしい方法を考え出した。")
    );

    public static final List<Component> LAMENT_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("黒い服は死を嘆く人たちの為のものだ。"),
            Component.literal("その哀悼にはただおごそかな気持ちだけいなければならないのだから、華やかな装身具は一切必要ない。"),
            Component.literal("砂漠の真ん中に小高い丘が一つ見えたらどうか荒らさないでくれ。"),
            Component.literal("ここで死んでいった数多くの蝶の墓だ。")
    );

    public static final List<Component> LOGGING_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("木や人間など多彩な対象を伐採切断するのに適した斧。"),
            Component.literal("手入れを怠っていないのか、斧の刃は常に鋭い。")
    );

    public static final List<Component> MAGIC_BULLET_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("元の力を完全に引き出すことこそできないものの、それが保持している魔力は依然として強力である。"),
            Component.literal("弾丸は地平線に沿って廊下を横切る。")
    );

    public static final List<Component> MK4_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("コートのあちこちに出所不明の血痕が見える。"),
            Component.literal("一部のエージェントは血痕を自慢に思っている。"),
            Component.literal("しかし、その血痕はコアから抽出した時から存在する。"),
            Component.literal("それは過去に起きた、またはこれから起こりうる大惨事を連想させる。"),
            Component.literal("機械の反乱は現代社会の脅威ではない。")
    );

    public static final List<Component> AROMA_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("数回処理工程されたようにつやのある表面は丈夫である。"),
            Component.literal("しかし、圧倒的な精神攻撃が加えれられた場合、元の土くれが粉々に崩れる可能性がある。"),
            Component.literal("途中で生じる亀裂は花の山で覆われる。")
    );

    public static final List<Component> BLUE_SCAR_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("果てしない闘争の歴史を表すがごとく、無数の傷がついている。"),
            Component.literal("あらゆる傷が時と共に痛みが和らぐように、すべてのダメージを完璧に防げはしないがその痛みを和らいでくれるだろう。")
    );

    public static final List<Component> CRIMSON_SCAR_ARMOR = List.of(
            Component.literal("特殊能力：付近に敵が居る場合、移動速度が30%上昇する。"),
            Component.literal("赤ずきんの傭兵が唯一愛せるのはオオカミの死だけだ。"),
            Component.literal("破滅でしか生きる価値を感じられない者の先には闇だけが広がっている。"),
            Component.literal("時には背負った荷物を下ろすように、長年の憎悪も一度下ろしてみても良いかもしれない。")
    );

    public static final List<Component> HATRED_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("魔法少女の愛と正義を注ぎ込んだ魔法のドレス。"),
            Component.literal("それを身に着けたなら、正義の魂と世界を守る願望が湧き上がるかもしれない。"),
            Component.literal("同時に、愛の奥底に沈む憎しみの音を聞くだろう。"),
            Component.literal("私たちは何をすればいいの？世界のすべてを愛そうとしたけど、残ったのは壊れた心だけ。")
    );

    public static final List<Component> HEAVEN_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("ただ、君の視線の中に留めておいたんだ。"),
            Component.literal("古い神が向けた翼を広げた瞬間、あなただけの天国が掘り起こされる。")
    );

    public static final List<Component> HORNET_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("王国は歴史に名を残すだろうが、誰が蜂たちの犠牲を覚えているだろうか？"),
            Component.literal("一時の栄光は烙印のように残っている。")
    );

    public static final List<Component> LAMP_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("森の仲間を救うほど、大鳥の目も1つずつ増えていった。"),
            Component.literal("この防具にはその輝かしい誇りが表れている。"),
            Component.literal("燃えるような無数の目はいつもある場所を向いている。")
    );

    public static final List<Component> STEM_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("誰も来ないことを悟った途端、魔法のように茎と葉が芽吹いた。"),
            Component.literal("張り巡らされた茎はその悲惨な確信を刻みつけている。"),
            Component.literal("強い決意は苦しみを和らげるだろう。"),
            Component.literal("E.G.Oの製造過程で生み出される毒気を抜き去ることに課題があった。")
    );

    public static final List<Component> SWAN_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("濃い煙の中で彼らは幻のような日々を繰り返した。"),
            Component.literal("辛くて足掻くのにも飽きたと思う度に、思い出の詰まったブローチがその心を抑えた。"),
            Component.literal("汚れた湖の底にある水草は、黒鳥の羽のようにぬめっていた。")
    );

    public static final List<Component> TEARS_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("灰のように落ちた涙は星座のように彩っている。"),
            Component.literal("着用者は悲しみについて考えるようになる。"),
            Component.literal("時には何もないことにも時折涙を浮かべている日もあるだろう。"),
            Component.literal("すべての悲しみがそうであるように、分かち合うほどに悲しみの重荷が軽くなっていないだろうか。")
    );

    public static final List<Component> DA_CAPO_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("交響曲を演奏する時にふさわしく素晴らしい燕尾服。"),
            Component.literal("完璧な合奏団をまとめあげるには卓越したリーダーシップが必要である。"),
            Component.literal("拍手がなくならない程度でコンサートを終わらせるというのはどうだろうか。")
    );

    public static final List<Component> JUSTITIA_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("他のものと同じように、最初は希望が存在した。"),
            Component.literal("平和への願いは今やおとぎ話にしか存在しない。"),
            Component.literal("このE.G.Oを抽出したエージェントは、会社内で最も公平な人物だった。"),
            Component.literal("包帯を外してはならない。"),
            Component.literal("それは見られてはならない過去の悲しい思い出を隠している。")
    );

    public static final List<Component> MIMICRY_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("人を守るためには人間の皮が必要である。"),
            Component.literal("たとえ人の肌ではなくとも、それに似た肌は私たちを守ってくれている。"),
            Component.literal("着用した職員たちは、それでも持っていた人類愛を失うかもしれない。")
    );

    public static final List<Component> PINK_ARMOR = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("ピンクの軍服。"),
            Component.literal("抜群の収納能力で多種多様な弾薬/弾倉を携帯できます。"),
            Component.literal("心が平穏になっていきます。"),
            Component.literal("ピンク色は多くの人たちに、精神的な安らぎをもたらすのですから。")
    );

    public static final List<Component> SMILE_ARMOR = List.of(
            Component.literal("特殊能力：モブを倒した際にドロップ品を拾うと、体力が全回復する。"),
            Component.literal("見慣れた顔が刻まれている。"),
            Component.literal("着用者は突然死の重さを感じる。"),
            Component.literal("敵の攻撃は死んだ人々が代わりに防ぐから安心しても大丈夫だろう。"),
            Component.literal("時々、辛く苦しむようなうめき声が聞こえるが、もはや与えられる救いは何もないので無視しよう。")
    );

    public static final List<Component> STAR_ARMOR = List.of(
            Component.literal("1特殊能力：10秒ごとに、自分と周囲の味方のHPを5回復する。"),
            Component.literal("心臓のあたりを神秘的な光が照らす。"),
            Component.literal("点滅するのではなく、輝いている。"),
            Component.literal("注意深く見れば、いつか帰るべきある日が見える。")
    );

    public static final List<Component> TWILIGHT_ARMOR = List.of(
            Component.literal("特殊能力：同名の武器を装備しているなら、HPが1減るごとに攻撃力が10%上昇する。"),
            Component.literal("特殊能力：5秒間に1回、周囲5ブロックの敵にスリップダメージを6与える"),
            Component.literal("怪物を倒そうとする3羽の鳥の努力は一つになった。"),
            Component.literal("数多くの悲劇を防ぐ事はできるだろう。"),
            Component.literal("しかし、黒い森の中に踏み入れる覚悟をしなければならない。")
    );

    public static final List<Component> WHITENIGHT_ARMOR = List.of(
            Component.literal("特殊能力：装備時、5以下のダメージを無効化。"),
            Component.literal("特殊能力：同名の武器を同時に装備している際、10以下のダメージを吸収。"),
            Component.literal("恐れるな。"),
            Component.literal("汝の祈りは我に届いた。"),
            Component.literal("苦痛はどう思うかに過ぎないとなぜ気づかぬ。"),
            Component.literal("汝ら、奇跡による証明を望むか。"),
            Component.literal("我を信じ、新たな命を得るといい。"),
            Component.literal("私がその権能を示そう。")
    );

    public static final List<Component> PENITENCE_WEAPON = List.of(
            Component.literal("特殊能力：攻撃的中時、HPを2回復する。"),
            Component.literal("この武器は後悔と苦労を微かに帯びている。"),
            Component.literal("この武器を作るための元型は慎重に作られた。観測はこの武器の成功の鍵だった。"),
            Component.literal("窪んだ眼窩は魂を眺め、すべての罪を見ることができる。"),
            Component.literal("この武器を使うためにはより良い善のために一つだけ虐待を行わなければならない。"),
            Component.literal("この武器は他のE.G.Oよりも強くはないが、武器を使用する人に精神的な快適さを与える。"),
            Component.literal("しかし、公正さが欠如した人々には何も与えられない。")
    );

    public static final List<Component> SODA_WEAPON = List.of(
            Component.literal("攻撃力2 | 射程：長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("エビが非常に好きだった職員によって最終的に抽出された武器。"),
            Component.literal("グレープのような紫色のピストルで、発射時に微かにグレープの香りが空中に広がる。")
    );

    public static final List<Component> WINGBEAT_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("妖精たちの加護を受けた武器は薄い光で輝いている。"),
            Component.literal("翼のコンパクトな見た目とは異なり、E.G.O自体はずっしり重い。")
    );

    public static final List<Component> BATH_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("まるで、永遠に流れる血にまみれた手がナイフを持ったかのような血痕がある。"),
            Component.literal("ダメージを受ける者の苦痛まで減らそうとする狙いだったのか、その刃はとても鋭く再生すらできないほど骨を綺麗に切断する。"),
            Component.literal("また、それは幸せになりたいという欲求まで切り裂き、再び戻ることはない。")
    );

    public static final List<Component> BEAK_WEAPON = List.of(
            Component.literal("攻撃力2 | 射程：長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("その威力を比較するためには大きさが何の問題もなかったように、その小ささにも関わらず火力は強力である。"),
            Component.literal("前に立ちはだかってくる物に対して一抹の容赦も必要ないかのように大量に発射しよう。"),
            Component.literal("銃弾の表面は何十個の小さな歯の集まりのように鋭く、敵にひどい痛みを与えることができる。")
    );

    public static final List<Component> LANTERN_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("皓々と光る発光体は暗闇を容易の中を照らすことができる。"),
            Component.literal("また、闇の中で餌の役割を果たすのにも優れている。"),
            Component.literal("しかし、鎧から伸びる歯はとてもリアルで恐ろしい。")
    );

    public static final List<Component> MATCH_WEAPON = List.of(
            Component.literal("攻撃力20 | 射程：超長 | 超低速"),
            Component.literal("特殊能力なし"),
            Component.literal("人類の初めの火のように炎がメラメラと燃えあがる。"),
            Component.literal("炎は幸せ、温かさ、世界の輝きを燃やしつくすまで消えることはない。"),
            Component.literal("もちろん武器の実験の犠牲者は避けられなかった。"),
            Component.literal("焼かれている人々は意識が燃え尽きるまで世界への無限の憎悪だけを感じるだろう。")
    );

    public static final List<Component> RED_EYE_WEAPON = List.of(
            Component.literal("特殊能力：HPが満タンの時、移動速度が50%上がる。"),
            Component.literal("母なるクモは多くの目があり、その子供は貪欲で飢えている。"),
            Component.literal("その目は食べ物を探すために夜になると輝く。"),
            Component.literal("この E.G.O はその直感を利用して武器の追跡能力を強化する。"),
            Component.literal("暗闇の中で犠牲者を見つけるために赤く輝くオーラを持つ。"),
            Component.literal("しかし、距離が長すぎると、蜘蛛が邪魔になることがある。")
    );

    public static final List<Component> REGLET_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("人類の未来を変える可能性を秘めた秘密研究は地下で開始された。"),
            Component.literal("非人道的でありながらも、全てはより大きな目標のために後悔なく尊厳は傷つけられた。"),
            Component.literal("心の優しいカルメンでさえもそれを大目に見た。"),
            Component.literal("この後悔により砕かれた敵は二度と正常な生活には戻れない。")
    );

    public static final List<Component> SOLITUDE_WEAPON = List.of(
            Component.literal("攻撃力3 | 射程：長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("原型の形でも孤独の根深い存在感はいつまでも残っている。"),
            Component.literal("銃弾は敵の骨を貫く代わりに、その心に永遠に満たされない空虚を残す。"),
            Component.literal("抽出時点であちこちが既に錆びついていた。")
    );

    public static final List<Component> SOMEWHERE_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("理解しようとせず、使用のみに留めるように。"),
            Component.literal("槍は使用者をたびたび無限の精神領域に導こうと試みる。"),
            Component.literal("しかし、使用者は惑わされてはいけない。"),
            Component.literal("その形態はすべての抽出時で変化した。"),
            Component.literal("そして、数々の試みの末、最終的に固定化した。"),
            Component.literal("この槍は、別の宇宙からのエコーを聞くと明るい光を放つという噂がある。")
    );

    public static final List<Component> TODAY_WEAPON = List.of(
            Component.literal("攻撃力2 | 射程：長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("様々な表情が布で埋められている。"),
            Component.literal("顔の表情を見せないことは、恥ずかしがりのようなものかもしれない。"),
            Component.literal("もう自分の気持ちを隠すことができないと感じたら、あなたの顔を隠そう。")
    );

    public static final List<Component> BEAR_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("かつては、ふわふわだったかもしれないが、今ではボロボロに見えるだけである。"),
            Component.literal("修復要求は却下された。"),
            Component.literal("抱擁の暖かい記憶は、それが捨てられたときに忘れ去られた。"),
            Component.literal("忘れ去られた純真さのようなクマの詰め物がはみ出している。")
    );

    public static final List<Component> BLOOD_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("足を切断した後でさえ、信じられない信念で森へ行き、元の場所に戻ろうとする。"),
            Component.literal("美しいレースは、美しい笑顔の女の子を連想させる。"),
            Component.literal("悲劇を繰り返さないように注意しなければならない。"),
            Component.literal("おそらくいつか、血まみれの靴が素晴らしい舞踏場へ向かわせるだろう。")
    );

    public static final List<Component> GALAXY_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("あなたの手の小石は、輝き、揺れ、くすぐり、それは宇宙になる。"),
            Component.literal("小石の中に宇宙があります。子供が泣けば星が生まれます。あなたの宇宙に私はいますか？")
    );

    public static final List<Component> HARMONY_WEAPON = List.of(
            Component.literal("攻撃力30 | 射程：長 | 超低速"),
            Component.literal("特殊能力なし"),
            Component.literal("ただの錆びていく機械に見えるかもしれないが、奏でる音はどんな楽器よりも聴くものを魅了する。"),
            Component.literal("代わりに使用者は自らを捧げなければならない。"),
            Component.literal("元来、芸術とは絶望と苦痛によってのみ生み出される悪魔の贈り物なのだ。"),
            Component.literal("その身が粉となり崩れ去るまで演奏を止めるなかれ。")
    );

    public static final List<Component> HARVEST_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("知恵に憧れた最後の遺産。"),
            Component.literal("長い鍬は、新しい畑の代わりに人間の脳を耕した。"),
            Component.literal("この鍬の為にどれだけ多くの聡明な命が失われたことだろうか？。")
    );

    public static final List<Component> ICE_SHARD_WEAPON = List.of(
            Component.literal("特殊能力：攻撃的中時、対象の移動速度を30%低下させる。"),
            Component.literal("雪の女王は美しい、けれども心臓があるべきことろは空っぽで凍りついていた。"),
            Component.literal("槍の穂先は実直であり同時に冷ややかである。"),
            Component.literal("対象に攻撃をすると一瞬で自らを無くしてしまうだろう。"),
            Component.literal("というのも、槍は雪で作られており、一日で溶けてしまう。"),
            Component.literal("雪が溶ける日が来たら、心も溶けてしまう。")
    );

    public static final List<Component> LAETITIA_WEAPON = List.of(
            Component.literal("攻撃力7 | 射程：超長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("制御には時間が必要だが、その威力は無視できない。"),
            Component.literal("荒削りのデザインは古代に制作されたように見える。"),
            Component.literal("小さなアクセサリーは、まるで無邪気さに憧れる子供の願いのように残っている。")
    );

    public static final List<Component> LAMENT_WEAPON = List.of(
            Component.literal("攻撃力2 | 射程：長 | 超高速"),
            Component.literal("特殊能力：オフハンドに色違いの同名アイテムを持つと二丁拳銃として使用可能。"),
            Component.literal("哀悼する気持ちには一切れの軽薄でさえも許されないように厳粛だ。"),
            Component.literal("一つは死者への悲痛のために、一つは生者への哀悼のために。")
    );

    public static final List<Component> LOGGING_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("木や人間など多彩な対象を伐採切断するのに適した斧。"),
            Component.literal("手入れを怠っていないのか、斧の刃は常に鋭い。")
    );

    public static final List<Component> MAGIC_BULLET_WEAPON = List.of(
            Component.literal("特殊能力：同名の防具を装備していれば、この武器による攻撃力が3上がる。"),
            Component.literal("特殊能力：ブロックに当たり判定がないため、壁越しのモブにも弾丸が的中する。"),
            Component.literal("WARN：味方にも攻撃が当たるフレンドリーファイア仕様です。Configで無効化されないので注意。"),
            Component.literal("元の力を完全に引き出すことこそできないものの、それが保持している魔力は依然として強力である。"),
            Component.literal("弾丸は地平線に沿って廊下を横切る。")
    );

    public static final List<Component> MK4_WEAPON = List.of(
            Component.literal("特殊能力：右クリックで多段ヒット攻撃が可能。"),
            Component.literal("グラインダーの鋭い歯は、敵をきれいに切断する。"),
            Component.literal("操作は簡単だが、武器として使用できるようにするのは簡単ではなかった。"),
            Component.literal("機械には独自の価値観がないため、善と悪の明確な境界はない。"),
            Component.literal("これはまさにこの武器が信頼できる理由である。"),
            Component.literal("機械の反乱は現代社会の脅威ではない。")
    );

    public static final List<Component> AROMA_WEAPON = List.of(
            Component.literal("攻撃力10 | 射程：超長 | 普通"),
            Component.literal("特殊能力なし"),
            Component.literal("E.G.Oの抽出でもその元になった香りを隠すことはできない。"),
            Component.literal("持っているだけで、あなたが知らない森に立っているような錯覚を起こす。"),
            Component.literal("矢じりは尖ってないが、大地に落ちると鮮やかな花が咲くだろう。"),
            Component.literal("すべての人の欲望が花へと変化したとき、この武器はもはや必要ないだろう。")
    );

    public static final List<Component> BLUE_SCAR_WEAPON = List.of(
            Component.literal("特殊能力：自身のHPが50%以下になると攻撃力が50%上昇。"),
            Component.literal("WARN：強化中は魔弾同様Config制御不可のフレンドリーファイアが適用されます。"),
            Component.literal("凶悪なオオカミの爪を連想させるガントレット。"),
            Component.literal("かつては、この爪で多くの命の腹が裂かれ、内臓をぶち撒かれただろう。"),
            Component.literal("相手の肉を切り刻み、傷から血が止めどなく流れていった。")
    );

    public static final List<Component> CRIMSON_SCAR_GUN_WEAPON = List.of(
            Component.literal("攻撃力13 | 射程：中 | 高速"),
            Component.literal("特殊能力：オフハンドに同名の武器を持っているなら、自身のHPが50%以下になると攻撃力が50%上昇。"),
            Component.literal("片手には鉄を、片手には火薬を持てば、この世に恐れるものはない。"),
            Component.literal("中途半端な勇気より、憎悪に満ちた躊躇なき一撃が重要だ。"),
            Component.literal("この果てしなく愛らしい童話の物語も、いつかは終われる事を願う。")
    );

    public static final List<Component> CRIMSON_SCAR_SCYTHE_WEAPON = List.of(
            Component.literal("特殊能力：オフハンドに同名の武器を持っているなら、自身のHPが50%以下になると攻撃力が50%上昇。"),
            Component.literal("片手には鉄を、片手には火薬を持てば、この世に恐れるものはない。"),
            Component.literal("中途半端な勇気より、憎悪に満ちた躊躇なき一撃が重要だ。"),
            Component.literal("この果てしなく愛らしい童話の物語も、いつかは終われる事を願う。")
    );

    public static final List<Component> DIFFRACTION_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("このE.G.Oを見るには極度の集中力が必要だ。"),
            Component.literal("心の目で見ろ！などの的外れな助言は無視していい。"),
            Component.literal("見えない分、使用時に範囲をよく計算した方がいい。"),
            Component.literal("少なくない数の職員が、振り回したこのE.G.Oで負傷している。")
    );

    public static final List<Component> HATRED_WEAPON = List.of(
            Component.literal("攻撃力5-8 | 射程：超長 | 超高速"),
            Component.literal("特殊能力：味方に的中時、HPを乱数で5-8回復。"),
            Component.literal("魔法少女のラブリーなパワーが溢れる魔法のステッキです。"),
            Component.literal("悪い奴らはみんな聖なる光を浴びて、身も心も浄化されて生まれ変わります。"),
            Component.literal("燃えゆくまま、永遠に目が覚めなければと願ったであろう。"),
            Component.literal("みんなを守りたかった愛の形は、執着となって心を蝕み始めた。"),
            Component.literal("それに気づき正そうとした時は、すべてが終わっていた。")
    );

    public static final List<Component> HEAVEN_WEAPON = List.of(
            Component.literal("特殊能力：右クリックで投擲可能(ダメージ300)。"),
            Component.literal("WARN：使い捨ての投擲なので投げると消失します。"),
            Component.literal("ただ、君の視線の中に留めておいたんだ。"),
            Component.literal("古い神が向けた翼を広げた瞬間、あなただけの天国が掘り起こされる。")
    );

    public static final List<Component> HORNET_WEAPON = List.of(
            Component.literal("攻撃力5-8 | 射程：超長 | 超高速"),
            Component.literal("特殊能力なし"),
            Component.literal("王国は永続的に復興しなければならず、それにはもっと多くの働き蜂が必要だった。"),
            Component.literal("王国は歴史に名を残るだろうが、彼女のために死んでいった蜂の犠牲を誰が記憶しているだろうか？"),
            Component.literal("弾丸は脅威となる対象のみに向かって飛んでいくので、銃で狙いを定める必要がない。"),
            Component.literal("必要なのは意志だけである。"),
            Component.literal("弾丸は離れた敵の元へと届き、発射された弾丸は過ぎ去った歴史を辿ることさえできる。")
    );

    public static final List<Component> LAMP_WEAPON = List.of(
            Component.literal("特殊能力：攻撃的中時、25%の確率で対象の耐性弱化。"),
            Component.literal("森の仲間を救う度に大鳥の目も増えていった。"),
            Component.literal("この武器には森の仲間を救ってきたという誇りが込められている。")
    );

    public static final List<Component> STEM_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("誰も来ないことを悟った途端、魔法のように茎と葉が芽吹いた。"),
            Component.literal("しかし生まれ持った悪意により、その花が咲いた時にすべての生命は崩れ落ちる。"),
            Component.literal("幹に触れて死ぬ敵はその鋭さというよりは、その妄執によって命が尽きる。"),
            Component.literal("この毒気の影響を防ぐために製造過程では徹底的にマスクを着用したまま行われる。")
    );

    public static final List<Component> SWAN_WEAPON = List.of(
            Component.literal("特殊能力：被ダメージ時、10%の確率で攻撃を反射する。"),
            Component.literal("いつか白くなると信じて、黒い白鳥はイラクサの繊維を紡ぎ続ける事で呪いを消し去ろうと試みた。"),
            Component.literal("最終的に残ったものといえば、すり減った傘くらいのものだった。"),
            Component.literal("夢に溺れてみても、現実とは非情なものだ。")
    );

    public static final List<Component> TEARS_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("素早く突き刺すことに適するように作られた剣。"),
            Component.literal("剣の才能がない者でもこのE.G.Oで瞬く間に敵を蜂の巣にできる。"),
            Component.literal("騎士道がそうであるように、戦闘中にはいかなる反則も無いが、慈悲も無い。")
    );

    public static final List<Component> DA_CAPO_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("鎌を振り回す姿は指揮者のように静かで控え目である。"),
            Component.literal("この曲に譜面があるのならば、終末を演奏する音楽だろう。"),
            Component.literal("この武器を使用するエージェントは誰も聞くことができない静かな音楽に浸される。"),
            Component.literal("指揮者はフィナーレまで休むことはない。"),
            Component.literal("しかし、すべてが終わったときに拍手する観客を少しは残しておいてもいいかもしれない。")
    );

    public static final List<Component> JUSTITIA_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("高鳥の天秤は、あらゆる争いにおいて罪の重さを量ることを疎かにしないと努力してきたことを暗示する。"),
            Component.literal("それが敵を裂くとき、さらに罪の痕跡も一掃するかもしれない。"),
            Component.literal("このE.G.Oを抽出したエージェントは、会社内で最も公平な人物だった。"),
            Component.literal("包帯を外してはならない。"),
            Component.literal("それは見られてはならない過去の悲しい記憶を隠している。"),
            Component.literal("本来の目的のように、それは平和をもたらすことを目指す。")
    );

    public static final List<Component> MIMICRY_WEAPON = List.of(
            Component.literal("特殊能力：攻撃的中時、ダメージの25%を自身の回復に充てる。"),
            Component.literal("人間の外見を模倣しようとした欲望だけが中途半端に残っている。"),
            Component.literal("この世のものではない眼差しがあなたを見るとき、あなたは戦慄するだろう。"),
            Component.literal("使用者が限界まで極めると、それを振い壊滅的な影響を与えることができる。")
    );

    public static final List<Component> PINK_WEAPON = List.of(
            Component.literal("攻撃力24 | 射程：超長 | 普通"),
            Component.literal("特殊能力なし"),
            Component.literal("温かさと愛の色はピンク色だと言われてます。"),
            Component.literal("でも、本当にその色が愛の色でしょうか？"),
            Component.literal("愛と平和は銃によって手に入れられるものでしょうか？")
    );

    public static final List<Component> SMILE_WEAPON = List.of(
            Component.literal("特殊能力：攻撃的中時、対象に束縛7を付与する。攻撃が5回的中すると、周囲のモブにも束縛5を付与する。"),
            Component.literal("特殊能力：装備中、攻撃対象のHPが0となるとき、HP上限と攻撃力が2ずつ上がる。"),
            Component.literal("Tips：この効果でステータスが上がるのは30(個別計算)まで。"),
            Component.literal("名も知らない職員たちの青白い顔と巨大な口がかかっている。"),
            Component.literal("振り下ろすと、対象に向けてアンコウのような口を開いて食べてしまう。"),
            Component.literal("そして満足はない。")
    );

    public static final List<Component> STAR_WEAPON = List.of(
            Component.literal("攻撃力18 | 射程：超長 | 高速"),
            Component.literal("特殊能力なし"),
            Component.literal("その星は絶望より輝く。"),
            Component.literal("郷愁が小さいボールとなって温かい光線を発する。"),
            Component.literal("光の中では、全てが平等だ。")
    );

    public static final List<Component> TWILIGHT_WEAPON = List.of(
            Component.literal("特殊能力なし"),
            Component.literal("永遠に閉じることのない目、"),
            Component.literal("すべての罪を測る天秤、"),
            Component.literal("どんなものも一口で飲み込むクチバシが黒い森の平和を守るように、"),
            Component.literal("これを扱う者も彼らと同じく平和をもたらすだろう。")
    );

    public static final List<Component> WHITENIGHT_WEAPON = List.of(
            Component.literal("特殊能力：左クリックで特殊攻撃時、自身にHP40分のバリアを付与。"),
            Component.literal("特殊能力：右クリックで通常攻撃時、使徒の武器を召喚する。"),
            Component.literal("Tips：使徒の武器は22-28のダメージを与えながら60%の速度低下を付与し、2秒その場に持続する。"),
            Component.literal("WARN：このアイテムがインベントリ内にある間、食料やポーションによる回復不可。"),
            Component.literal("汝は扉を叩き。"),
            Component.literal("今や扉は開いた。"),
            Component.literal("我は終焉より訪れ、つかの間の世界に留まる者。"),
            Component.literal("それと同時に、新世界への灯火を点ける者。"),
            Component.literal("愛する者どもよ、これから我が汝らに最善の道を見せよう。")
    );
}
