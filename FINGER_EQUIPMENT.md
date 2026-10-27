# 親指・人差し指の装備

NeoForge 1.21.1向け。追加アイテムはクリエイティブタブから取得でき、例えば `/give @s pm_equips:finger1_weapon1` でライフルを取得する。全登録IDは小文字。

## 氷結と弾薬

`pm_equips:effect_freeze`（氷結 / Freeze）は、レベルIで移動速度-10%、防御力-1、攻撃力-3。amplifierが1増えるごとに同じ量を加算する。`effect_freeze.png` を使用する。

| クラス | ID | 命中時の効果 |
| --- | --- | --- |
| ItemAmmoLarge（既存） | ammo_large | 出血I・5秒 |
| ItemAmmoThumbsIF | ammo_thumbs_if | 発火・5秒 |
| ItemAmmoThumbsFR | ammo_thumbs_fr | 氷結I・5秒 |
| ItemAmmoThumbsAP | ammo_thumbs_ap | 実際に減らしたHPの10%を追加ダメージ |
| ItemAmmoThumbsKB | ammo_thumbs_kb | 射撃方向へ最大5ブロック押し出す |

新規4弾薬は `data/pm_equips/tags/item/bullet.json` に登録。テクスチャは指定の `ammo_thumb_if/ice/ap/kb.png` を使用する。

徹甲弾の追加分は防御・耐性・エンチャント軽減と通常のIフレームを無視する専用ダメージを通す。通常部分の実ダメージを基準にし、NPCのレベル補正を追加分へ重ねて加算しない。無敵設定・PvP・NPC保護などのダメージ拒否は維持する。衝撃弾はブロック衝突を通して押し出し、壁の中へ通さない。

## 親指のライフル

- `Finger1Weapon1` / `finger1_weapon1`：近接攻撃力3、攻撃速度1.2、近接の特殊効果なし。
- `Finger1Weapon2` / `finger1_weapon2`：近接攻撃力8、攻撃速度1.2、近接命中で出血IIを5秒。
- 両方とも耐久2000。近接命中・遠隔発射でそれぞれ耐久を1消費する。
- 右クリックの遠隔射撃はダメージ7、CT40tick、射程128ブロック。壁に遮られ、最初の有効な生物に命中する。
- Shift+Vで通常→火炎→氷結→徹甲→衝撃→通常の順に弾薬を選択する。選択した弾薬がなければ発射しない。選択は各ItemStackに保存する。
- Shift+Cで、インベントリにある使用可能な特殊弾を各種類1個消費し、その場で複合射撃する。特殊弾が1種類もなければ発動しない。通常弾は消費しない。
- 複合射撃は `17 + 消費した種類数` の基本ダメージと、その種類の特殊効果を全て適用する。4種類なら基本21、軽減のない対象への徹甲追加分は2.1。
- 遠隔射撃は両モデルで40tickのCTを共有する。弾薬不足・CT中のリクエストでは弾薬も耐久も消費しない。
- クリエイティブでは通常の無限素材仕様に従い弾薬を減らさない。NPCは選択中の弾薬を仮想的に供給し、実弾薬を渡さずに使用できる。

遠隔武器はそれぞれ `ProjectileWeaponItem` を直接継承する。弾丸エンティティを生成せず、サーバーで命中を決め、始点・終点・弾種を一つのパケットで通知してクライアントでパーティクルを描く。通常弾は白、火炎は橙、氷結は青、徹甲は黄、衝撃は紫。複合弾は使用した色を混ぜる。

Shift+Vはライフルを持っているときは弾薬切り替えを優先し、ライフルがなければ防具能力を処理する。キーは既存の設定画面から変更できる。

## 人差し指の剣

| クラス / ID | 攻撃力 | 攻撃速度 | 耐久 | 効果 |
| --- | ---: | ---: | ---: | --- |
| Finger2Weapon1 / finger2_weapon1 | 抜刀8・納刀4 | 1.5 | 1000 | 右クリックを離すと抜刀・納刀 |
| Finger2Weapon2 / finger2_weapon2 | 10 | 1.8 | 2000 | 命中前HPが最大の50%以下なら基本ダメージ+2 |
| Finger2Weapon3 / finger2_weapon3 | 12 | 0.8 | 2000 | 右クリックを押している間に剣ガード |

遂行者の剣は納刀時に攻撃や採掘で耐久を消費しない。納刀から40tick（通常TPSで2秒）以上押して離すと、パワーIVを100tick付与して抜刀する。この長押し能力だけに100tickのCTを付け、通常の抜刀・納刀は引き続き可能。抜刀表示は提供された `finger2_weapon1_caseless.png` を `finger2_weapon1.png` として参照し、納刀表示は `finger2_weapon1_casing.png` を使用する。

伝令の剣はガード成功時に通常の被ダメージを全て無効化して構えを解除し、60tickのCTに入る。CT中に構え直せず、無敵時間だけが残ることもない。虚空や管理コマンドなど無敵を貫通する強制ダメージは防がない。

## 制服

全6種は脚スロットの `ArmorItem` / `GeoItem` とし、全身のボーンを描画する。指定のモデル・防具テクスチャ・インベントリアイコンと `armor.animation.json` の `animation.animations.idle` を使用する。E.G.O防具と同じく、防具ごとに `Finger1Armor1M` / `Finger1Armor1R` などのモデル・レンダラーを用意し、`GeoModel` / `GeoArmorRenderer` を直接継承する。装備側の `GeoRenderProvider` もE.G.O防具と同じ形式で各レンダラーを生成する。

| クラス / ID | 名称 | 防御 / 強度 | 耐久 |
| --- | --- | ---: | ---: |
| Finger1Armor1 / finger1_armor1 | ソルダート | 12 / 12 | 1000 |
| Finger1Armor2 / finger1_armor2 | カポ | 15 / 15 | 2000 |
| Finger1Armor3 / finger1_armor3 | アンダーボス | 20 / 20 | 3000 |
| Finger2Armor1 / finger2_armor1 | 遂行者 | 15 / 15 | 1000 |
| Finger2Armor2 / finger2_armor2 | 代行者/伝令 | 18 / 18 | 2000 |
| Finger2Armor3 / finger2_armor3 | 神託代行者 | 20 / 20 | 3000 |

提供されたモデル名は `finger*_armor*.json` だったため、同じ内容を標準的な `finger*_armor*.geo.json` として用意し、形状IDも各モデル名に合わせて参照する。指定中の `finger1_armor2geo.json` は `finger1_armor2.geo.json` に統一している。

代行者/伝令の制服と代行者の剣を同時に装備すると、解禁Iを得る。伝令の剣ならShift+Vで、パワーI・忍耐I・クイックIから一つを選び、自分と半径16ブロックの味方へ400tick配布する。CTは600tick。味方は同じチーム、所有しているNPC・ペット、PvP無効時の他プレイヤー。1回の発動では全対象へ同じ種類を付与する。

神託代行者の制服とカドゥケウスを同時に装備するとクイックIを得る。剣はどちらの手にあってもセット判定に含める。カドゥケウスの強い解禁・クイックと競合しないよう同じ管理処理で調整し、装備を外すと武器・防具由来の効果を解除する。外部から付与された長時間・高レベルの効果を優先する。

## 検証

`FingerEquipmentTests` で属性・耐久、弾薬消費、通常/特殊/複合射撃、壁、徹甲の軽減無視とNPC補正、押し出し、39/40tickの抜刀境界、剣ガードの59/60tick、100tickの能力CT、599/600tickの配布CT、セット効果とカドゥケウスの共存を検証する。既存のNPC・柱・調律者防具・カドゥケウスも一緒に実行する。

```powershell
.\gradlew.bat -PnpcTests runNpcTest build --no-daemon --console=plain
```

ログは `build/finger-tests.log`、配布JARは `build/libs/pm_equips-1.21.1-6.3.jar`。実クライアントのモデル・キー入力・複数プレイヤーでの実プレイ確認は未実施。空JSON9件による読み込みエラーは2026-10-28に修正済み（試練の間の戦利品テーブル8件を標準データに復元し、W社タグを有効な空リストへ修正）。

2026-10-27に今回の17件と既存46件、計63件のGameTestが成功し、JARビルドが成功した。

APIは固定したNeoForge 21.1.264のソースと [1.21.1の公式通信仕様](https://docs.neoforged.net/docs/1.21.1/networking/payload/) を参照した。
