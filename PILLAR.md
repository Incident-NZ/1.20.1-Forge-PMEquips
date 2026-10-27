# 柱 / Pillar

エンティティIDは `pm_equips:p_arbiter_pillar`。`PArbiterPillar` は Minecraft 1.21.1 の `Projectile` を直接継承し、指定された `p_arbiter_pillar.geo.json` / `p_arbiter_pillar.png` を GeckoLib で描画する。

- 重力・空気抵抗・水中減速なし。発射方向に直進し、移動距離128ブロックで消える。発射元の移動速度を加算しない。
- 命中した生物に基本ダメージ11と妖精IIIを100tick付与する。同じ柱による直接ダメージは各対象に1回。通常の防御・無敵時間・PvP設定を尊重する。
- 移動する柱に触れた生物を進行方向へ押す。対象のブロック衝突を通して動かし、壁を貫通させない。
- ブロックに当たるとその位置に100tick留まる。半径3ブロックの生物へ妖精IIIを継続付与する。妖精の既存の周期ダメージを止めない間隔で更新する。
- 刺さる瞬間に押していた対象を拘束し、柱の消滅で解放して妖精VIを600tick付与する。通りかかっただけの範囲効果対象は拘束しない。
- 拘束はサーバーで管理し、プレイヤーには位置修正を送信する。対象のAI・重力・移動属性は変更しない。重複した柱は最後の拘束が終わるまで解放しない。
- 柱・対象の保存と読み込み、ログアウト、チャンクのアンロード、柱の途中削除、ディメンション移動で永久拘束を残さない。対象がアンロードされている場合は、再び読み込まれた際に期限を確認する。
- 射手、観戦者、クリエイティブのプレイヤー、PvPや既存NPCの保護対象には作用させない。
- 向きは上下を含む発射方向へ合わせる。モデルの長軸を向け、先端をブロックへの衝突位置に合わせる。
- トレイルIDは `pm_equips:arbiter_trail`。`particle_arbiter.png` を使う専用の発光・フェードするパーティクル。クライアントで経路を補間して生成し、個々の粒子をネットワーク送信しない。

## 呼び出し方

[調律者の防具](ARBITER_ARMOR.md) を脚に装着し、両手が空の状態で右クリックを2秒以上押して離すと発射できる。サーバー側の射撃処理から次のようにも生成できる（`PArbiterPillar` はimportして使用する）。速度は例として毎tick2ブロック。

```java
PArbiterPillar pillar = new PArbiterPillar(level, shooter);
pillar.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0, 2, 0);
level.addFreshEntity(pillar);
```

コマンドからの確認例（ワールドの+Z方向へ発射）：

```mcfunction
/summon pm_equips:p_arbiter_pillar ~ ~1.5 ~ {Motion:[0.0d,0.0d,2.0d]}
/particle pm_equips:arbiter_trail ~ ~1 ~ 0.2 0.2 0.2 0 10
```

## 検証

`src/npcTest` の `PArbiterPillarTests` で、全方向の直進・向き、射程と保存、命中ダメージ・妖精、押し出し・拘束、球状範囲、重複・途中削除、アンロード後の解放を確認する。通常のサーバーtickによる周期ダメージと自動解放、`ServerPlayer` とNetty仮想接続による位置修正パケットの送信、PvP設定による保護も検証する。

```powershell
.\gradlew.bat -PnpcTests runNpcTest build --no-daemon --console=plain
```

パーティクルは [NeoForge 1.21–1.21.1公式仕様](https://docs.neoforged.net/docs/1.21.1/resources/client/particles/) に従って登録する。実際の画面でのモデル・トレイルの見え方は別途クライアントで確認する。

2026-10-26時点で、柱の9件と既存NPCの19件、計28件のGameTestが成功。テスト用クラスは通常のJARに含めない。NPCの移動速度は作業開始時のソースにある0.4を維持し、既存の速度テストはその固定値を参照するように更新した。

2026-10-28に、試練の間の空の戦利品テーブル8件をMinecraft 1.21.1標準データから復元し、`wing_wcorp.json` を有効な空タグに修正した。これらの空JSON9件による読み込みエラーは解消済み。
