# `db/schema.sql`

H2用のスキーマ＋初期データ。冒頭に `DROP TABLE IF EXISTS` を並べているので、何度でも流し直せる。

## テーブル一覧

| テーブル | 主キー | 役割 | 現在使われている箇所 |
|---------|-------|------|---------------------|
| `personality`      | `personality_id` | 性格特性5つ（外向性・協調性・誠実性・情緒安定性・開放性） | `QuestionDAO.findQuiz` |
| `question`         | `question_id`    | 設問。`critical`（吉嵜）／`type`（NORMAL/FAKE/FRENEMY）／`personality_id` を持つ | `QuestionDAO.findQuiz` |
| `answer_select`    | `choice_id`      | 4段階の回答選択肢 | `QuestionDAO.findChoices` |
| `scoring`          | `(question_id, choice_id)` | 質問×選択肢ごとの素点。逆転質問はここで符号反転 | **未使用**（結果画面実装待ち） |
| `fixed_score`      | `(question_id, choice_id, personality_id)` | 「自分をフレネミーだと思いますか」で "とても当てはまる" のとき、5特性に固定スコアを入れる特殊ルール | **未使用** |
| `role`             | `id`             | 役職一覧。`is_black` で黒役職フラグ | **未使用** |
| `role_threshold`   | `(role_id, personality_id)` | 役職ごとの特性スコア範囲（正規化スコア −30〜+30） | **未使用** |
| `image`            | `image_id`       | 画像パス | **未使用** |
| `item`             | `item_id`        | おすすめアイテム | **未使用** |
| `account`          | `user_id`        | アカウント。`password` はハッシュ格納想定 | **未使用**（ログインはモック） |
| `result`           | `result_id`      | 診断結果。役職ID＋5特性スコア＋診断日時。`user_id` は未ログインなら NULL | `CounterDAO.Counter`（行数カウントのみ） |

## 設計上のポイント（schema.sql のコメントより）

- `question.personality_id` : 通常質問の特性。**特殊質問 (FAKE/FRENEMY) は NULL**
- `question.type` に `CHECK (type IN ('NORMAL', 'FAKE', 'FRENEMY'))`
- `scoring` の `score` は符号付き（逆転質問で負にする）
- `result.extraversion / agreeableness / conscientiousness / emotional_stability / openness` が診断後の5特性スコアを保存する列
- `role_threshold.ck_threshold_range` で `min_score <= max_score` を保証

## 初期データ

- 特性5件 (personality_id = 1..5)
- 選択肢4件 (choice_id = 1..4)
- 質問（百の位がカテゴリ: 1xx=外向性 … 5xx=開放性, 9xx=特殊）
- 役職や閾値などの実データは `schema.sql` の後半を参照

## 現状の実装状況

- **読む側だけ実装済み** : `question` / `personality` / `answer_select` を `QuestionDAO` が参照、`result` の行数を `CounterDAO` が参照
- **書き込みはまだ** : 診断結果の `result` INSERT や、`scoring` を使った集計はこれからの実装
