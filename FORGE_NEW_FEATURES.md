# NeoForge新規要素のForge 1.20.1移植

2026-10-28時点のNeoForge版から、新規NPC、柱、調律者防具、親指・人差し指の装備、氷結・解禁を移植した。既存のForge装備を維持し、新規要素にはNeoForge版と同じクラス名・登録ID・素材を使用する。

実行環境はMinecraft 1.20.1 / Forge 47.4.10 / Java 17。依存関係は既存プロジェクトのGeckoLib 4.7.4、Curios 5.14.1、AllTheOresを使用する。配布物は`build/libs/pm_equips-7.0.jar`。

## 追加要素と操作

- `mob_npc_1`：村のニート村人を置換する友好NPC。金による仲間化、左右の武器、1個の防具、レベル上限100、ランク5の先のEX、給餌による回復と大人2体による繁殖、7日間の子供期間、睡眠・拠点判定、所有者とPvPの保護を引き継ぐ。移動速度の基礎属性値は、移植元の現在の実装と同じ0.4固定。給餌の回復量は8＋レベル。
- NPCスキン：`config/pm_equips/npc_skins/`にPNGを配置する。`config/pm_equips-common.toml`の`npc.fixedSkin`で固定、`npc.includeDefaultSkins`で同梱スキンの使用、`npc.pvpDisposition`でPvP時の中立・敵対を選ぶ。外部スキンは接続プレイヤーへ同期する。
- `p_arbiter_pillar`：ダメージ11・妖精IIIを5秒・射程128。重力と空気抵抗なし。壁に5秒留まり、半径3の妖精付与、押し出し、拘束、解除時の妖精VIを30秒付与する。モデルの向き、専用トレイル、保存・読み込みと拘束期限を含む。
- `head_arbiter_armor`：脚防具として全身描画し、防御50・強度50・ノックバック耐性100%・不可壊。両手が空の状態で右クリックを短く押して離すとダメージ8・妖精Iを3秒のパーティクル弾、40tick以上押して離すと柱を撃つ。音声4種とチャージ取消条件も移植した。
- `finger2_weapon_sp_caduceus`：9形態を右クリックで切り替える。異なる3/6/9形態での命中による解禁I/II/III、連続命中の成長、空振りのリセット、持ち替え時の解除、形態ごとの属性・リーチ・処刑・耐久4000を維持する。
- `finger1_weapon1` / `finger1_weapon2`：親指のライフル。通常弾は既存の大口径弾、特殊弾は`ammo_thumbs_if/fr/ap/kb`。Shift+Vで弾薬を変更し、Shift+Cで所持する特殊弾を各1個ずつ使う複合射撃。通常射撃ダメージ7・CT40tick・射程128・耐久2000。射撃はパーティクルで表現する。
- `finger2_weapon1`：右クリックで抜刀・納刀。納刀状態から2秒以上押して離すとパワーIVを5秒付与して抜刀し、長押し能力に5秒のCT。
- `finger2_weapon2`：対象HP50%以下で追加ダメージ2。
- `finger2_weapon3`：右クリック長押しで全ダメージを防ぎ、成功後3秒のCT。
- `finger1_armor1/2/3`、`finger2_armor1/2/3`：脚防具・全身描画。指定の防御値・強度・耐久値、武器との組み合わせバフを維持する。人差し指防具2＋伝令の剣ではShift+Vで16ブロック以内の味方へ20秒のランダムバフ、CT30秒。
- `effect_freeze` / `effect_unlocked`：指定の属性補正、レベルによる増加、解禁の60tickごとの回復を実装する。

## Forgeへの適応

通信はForgeのSimpleChannelとサーバースレッドで処理する。新規パケットIDは32～36、通信プロトコルは2。同じ新バージョンをクライアント・サーバーの両方へ導入する。Minecraft 1.21のItemStackコンポーネントを1.20.1のNBTへ、イベントをForgeのイベントへ置き換えた。タグは1.20.1の`tags/items/`、ダメージ種別は`damage_type/`に配置する。

1.20.1に存在しない爆発ノックバック属性は、専用Mixinで装備中の生物の移動量とプレイヤー宛てのノックバック情報を保護する。防御・防具強度の上限を最低50へ引き上げる。通常のダメージ軽減式は維持する。防具のモデル・レンダラーは既存E.G.O防具と同じ個別のM/Rクラス構成で、新たな装備継承用クラスは追加していない。

NPCはForge 1.20.1の近接攻撃処理を左右の選択武器に合わせて評価し、それぞれの攻撃力・エンチャント・命中時効果・耐久消費を反映する。遠距離武器用の仮想弾薬は持ち物やドロップに残さない。徹甲弾の追加ダメージにNPCのレベル補正を重複加算しない。

## 検証

ForgeのGameTest専用サーバーで65件すべて成功した。NPC20件、柱9件、調律者防具10件、カドゥケウス9件、親指・人差し指装備17件。実際の属性、通常・爆発ノックバック、弾薬消費、両手攻撃とエンチャント、射程、拘束、保存、通信、バフ・デバフ、CT境界を検証する。ログは`build/forge-backport-gametest.log`。

```powershell
.\gradlew.bat -PfeatureTests runGameTestServer --offline --no-daemon --console=plain
node scripts/verify-forge-features.cjs
.\gradlew.bat build --offline --no-daemon --console=plain
```

テストコードは`src/featureTest/`にあり、配布JARには含めない。実クライアントでの描画・音声・キー入力と、複数の実プレイヤーによるプレイは未検証。
