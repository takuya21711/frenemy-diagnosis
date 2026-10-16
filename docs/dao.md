# `src/main/java/dao/`

DBアクセス層。すべて H2 Database の JDBC を直接使う（フレームワーク未使用）。
SQL の値はすべて `PreparedStatement` のプレースホルダで渡す（SQLインジェクション対策）。
失敗時は `printStackTrace()` して、空リスト / null / -1 を返す。

---

## `DbConfig.java`

**役割**: JDBC接続の共通設定。接続先は共用H2サーバー（TCPモード）に固定している（環境変数での切り替えはしない）。
H2 JDBCドライバのロードもここで一回だけ行う。

| 定数 | 値 |
|------|----|
| `URL`  | `jdbc:h2:tcp://10.23.104.249/~/frenemy` |
| `USER` | `sa` |
| `PASS` | `""` |

| メソッド | 概要 |
|----------|------|
| `getConnection()` | 上記の固定値で共用H2に接続して返す。全DAOはこれだけを使う |

> 接続先IPが変わったときは `DbConfig.java` の `URL` を書き換える。

---

## `QuestionDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `findQuiz()` | `List<Question>` | 出題リスト。各特性から通常質問をランダム5問 ＋ 特殊質問（フェイク・フレネミー）全部 ＋ 吉嵜質問1問 をシャッフル（今のデータだと28問） |
| `findChoices()` | `List<Choice>` | 4段階の選択肢 |

---

## `ScoringDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `findScoring(answers)` | `Map<質問ID, Map<選択肢ID, 点数>>` | 回答した質問の `scoring` を全部取る。失敗したら null |
| `findFixedScores(answers)` | `Map<特性ID, 固定スコア>` | 回答の中に `fixed_score` の組み合わせ（フレネミー質問 × とても当てはまる）があればその値。無ければ空。失敗したら null |

IN句は `?, ?, ?` を回答数だけ作って、値は全部プレースホルダで渡す。

---

## `RoleDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `findAll()` | `List<Role>` | 全役職をID順に、閾値（`role_threshold`）と一覧用の画像パス（その役職の `image` の1枚目）つきで返す |

---

## `ResultDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `insert(roleId, scores, userId)` | `int` | 結果を保存して `result_id` を返す。画像とおすすめアイテムは、その役職のものからランダムに1つ選んで入れる。`scores`（吉嵜のとき）と `userId`（未ログインのとき）は null 可。失敗したら -1 |
| `findById(resultId)` | `Result` | 役職・画像・アイテムを JOIN して1件。スコアがあれば特性名（`personality`）をつけて `traits` に並べる |
| `findByUser(userId)` | `List<Result>` | マイページの履歴（新しい順） |
| `hasRole(userId, roleId)` | `boolean` | そのユーザーがその役職を出したことがあるか（吉嵜の解放判定） |

`result` の5特性の列名は schema で固定なので、`SCORE_COLUMNS`（添字＝特性ID）で対応させている。

---

## `AccountDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `findByName(name)` | `Account` | 名前で探す。無ければ null |
| `insert(name, passwordHash)` | `int` | 登録して `user_id` を返す。失敗（名前の重複も含む）したら -1 |

---

## `CounterDAO.java`

| メソッド | 戻り値 | 概要 |
|----------|--------|------|
| `Counter()` | `int` | `SELECT COUNT(*) FROM result`（＝診断人数。吉嵜も含む）。失敗時は -1 |
