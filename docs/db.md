# `db/schema.sql`

H2用のスキーマ＋初期データ。冒頭に `DROP TABLE IF EXISTS` を並べているので、何度でも流し直せる。
**テーブル構成を変えたので、既存のDBにも流し直してください**（`image` / `item` に `role_id` を追加）。

## テーブル一覧

| テーブル | 主キー | 役割 | 使っている箇所 |
|---------|-------|------|---------------|
| `personality`      | `personality_id` | 性格特性5つ | `QuestionDAO`, `ResultDAO`（特性名） |
| `question`         | `question_id`    | 設問。`critical`（吉嵜）／`type`（NORMAL/FAKE/FRENEMY）／`personality_id` | `QuestionDAO` |
| `answer_select`    | `choice_id`      | 4段階の選択肢 | `QuestionDAO` |
| `scoring`          | `(question_id, choice_id)` | 質問×選択肢の点数（+5/+3/-3/-5）。逆転質問は符号反転。特殊質問は 0 | `ScoringDAO` |
| `fixed_score`      | `(question_id, choice_id, personality_id)` | フレネミー質問 × とても当てはまる のときの固定スコア | `ScoringDAO` |
| `role`             | `id`             | 役職15個と説明文。`is_black` で黒の役職（人狼・狂人） | `RoleDAO`, `ResultDAO` |
| `role_threshold`   | `(role_id, personality_id)` | 役職ごとの特性スコアの範囲（-30〜+30）。吉嵜(15)は無し | `RoleDAO` |
| `image`            | `image_id`       | 役職の画像パス（`role_id` つき） | `ResultDAO`, `RoleDAO` |
| `item`             | `item_id`        | おすすめアイテム（`role_id` つき、1役職に複数可） | `ResultDAO` |
| `account`          | `user_id`        | アカウント。`password` は PBKDF2 の `salt:hash` | `AccountDAO` |
| `result`           | `result_id`      | 診断結果。役職・画像・アイテム・5特性スコア・日時・`user_id`（未ログインは NULL）。吉嵜のときスコアは NULL | `ResultDAO`, `CounterDAO` |

## データを変えたいとき

コードは触らず、`schema.sql` を書き換えて流し直せば反映されます。

- 質問を足す・変える → `question`（`scoring` は INSERT ... SELECT で自動生成。逆転質問は IN の一覧に足す）
- 役職の説明 → `role.description`
- 閾値 → `role_threshold`
- おすすめアイテム → `item`（今のものは仮）
- 画像 → `image.path`（今は `image/role/{id}.svg` の仮画像）
