# NeoForge 1.21.1 再移行

2026-10-24、隣接する `1.20.1-pm-equips` の実際の作業ツリー（Mod 6.2）を基準に再移行しました。旧1.21.1版の `src` と生成物を置き換え、移行元のソースや既存の `run` 内のワールドは編集していません。

## 実行環境

| 依存関係 | 使用バージョン |
| --- | --- |
| Minecraft / Java | 1.21.1 / 21 |
| NeoForge | 21.1.264 |
| ModDev Gradle / Gradle | 2.0.148 / 8.14.5 |
| GeckoLib | 4.9.3 |
| Curios | 9.5.1+1.21.1 |
| JEI（任意） | 19.69.0.457 |
| AllTheOres | 3.2.0（CurseForge file 7825464） |

配布JARには依存Modを同梱していません。実行環境にはNeoForge、GeckoLib、Curios、AllTheOresが必要です。JEIは任意です。

## 移行と整理

- 移行元のクラス名、登録ID、装備区分、数値、モデル、レシピを基本として維持しました。登録アイテム190件（ブロックアイテム3件を含む）、ブロック3件、エンティティ3件です。
- Forgeイベント、FE capability、通信、レジストリ、メニュー、エフェクト、GeckoLib描画をNeoForge 1.21.1のAPIへ変更しました。通信処理ではサーバー側で手持ち装備、メニュー、距離、素材を検証します。
- ItemStackの独自NBTは1.21.1の `CUSTOM_DATA` componentへ移行しました。書き込み処理を共通の静的ヘルパーへ集約しています。
- 継承用の新規クラスは追加していません。既存の遠距離武器は引き続きMinecraftの `ProjectileWeaponItem` を直接継承します。
- データパックのフォルダ名とレシピ出力形式を更新し、共通タグを `forge:` から `c:` へ変更しました。Curiosのbadgeスロットは現行のデータ形式で定義しています。
- 使用されないenergyクラス、空のMixin4件、タブアイコンを書き換えるリフレクションを削除しました。タブアイコンはbuilderで指定します。Javaファイル数は326件から321件になりました。
- 型はimportして使用し、ソース内の完全修飾型名、不要・重複importを整理しました。

## 発見して修正した不具合

- 全身防具が脚スロットで正しい防御力を持つよう、旧防具素材の防御値を脚用へ割り当てました。耐久値、強度、ノックバック耐性は移行元の値を使用します。
- 製造結果を一部だけインベントリへ格納できた場合の重複ドロップを修正しました。
- 製造時にオフハンドの素材を判定する一方で消費しない問題、重複した素材エントリによる必要数不足を修正しました。
- クライアントのレシピ同期が統合サーバーのレシピキャッシュを書き換えないよう、キャッシュを分離しました。ログイン時とデータ再読み込み時に同期します。
- FEの負数・容量超過を制限し、ツールチップ表示時のデータ変更と、使い切った電池のスタック処理を修正しました。
- BlueScarの継続ダメージ処理をサーバーで実行し、WCorp回避効果の終了処理を現行のエフェクトイベントへ移行しました。
- 古い `pm_equips:e_gen` 参照を登録済みの `pm_equips:pe_box_generator` に修正しました。
- ローランの結晶武器は、耐久付きアイテム2個を1スタックで生成すると1.21.1でレシピが拒否されるため、出力1個・素材半量へ変更しました。1本あたりの素材費は同じです。
- `item.roland_mook_attack` のサウンド定義を登録IDと一致させ、字幕の翻訳キーを追加しました。

## 確認結果

静的照合は `node scripts/verify-migration.cjs` で再実行できます。移行元との登録ID・継承関係、JSON、タグ、レシピ、モデル、テクスチャ、音声、字幕を確認します。基準データは `scripts/source-baseline.json` です。

実行確認は既存ワールドから隔離した `build/migration-server` と `build/migration-client` で行いました。

- Javaコンパイル、リソース処理、JAR生成に成功。
- 静的照合：JSON 499件、通常レシピ102件、エラー0件。
- 専用サーバー起動、通常レシピ1764件（依存Mod・バニラを含む）、独自製造レシピ85件の読み込みに成功。
- クライアント接続、JEI初期化、リソース読み込み、`reload` 後の接続維持を確認。最終起動ログにPMEquipsのERRORなし。
- `lobego3_suit_magic` の脚スロット装備で防御力20・防具強度20をサーバーのattributeコマンドから確認。インベントリ画面で全身モデルの表示も確認。
- `save-all flush` と `stop` でサーバーの保存・正常終了を確認。

画面操作はユーザーのEscape操作で停止しました。魔法の弾丸の実射・弾薬消費、全武器の戦闘挙動、FE転送の実操作、複数クライアントの同時接続による長時間試験は未実施です。起動・接続確認だけで全ゲームプレイの動作保証はしていません。

ビルドは `./gradlew.bat build`。出力先は `build/libs/pm_equips-1.21.1-6.2.jar`、検証記録は `build/migration-verification.json`、`build/migration-build.log`、`build/migration-runtime-checks.jsonl` と各検証用ゲームディレクトリの `logs/latest.log` です。

## 確認に使用した公式資料

- [NeoForge 1.21.1 Capabilities](https://docs.neoforged.net/docs/1.21.1/inventories/capabilities/)
- [NeoForge 1.21.1 Networking](https://docs.neoforged.net/docs/1.21.1/networking/payload/)
- [1.20.5 component・ItemStack変更点](https://docs.neoforged.net/primer/docs/1.20.5/)
- [ModDevGradle](https://github.com/neoforged/ModDevGradle)
- [AllTheOres 3.2.0](https://www.curseforge.com/minecraft/mc-mods/ato/files/7825464)
