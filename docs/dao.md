# `src/main/java/dao/`

DBアクセス層。すべて H2 Database の JDBC を直叩き（フレームワーク未使用）。

---

## `DbConfig.java`

**役割**: JDBC接続の共通設定。環境変数から `DB_URL` / `DB_USER` / `DB_PASS` を読み、
未設定なら「学校のパソコンのデータベース」にいく。
H2 JDBCドライバのロードもここで一回だけ行う。

### クラス `DbConfig`

- `final`・`private` コンストラクタ でインスタンス化不可（ユーティリティクラス）
- static イニシャライザで `Class.forName("org.h2.Driver")` を実行

| 定数 | 値 |
|------|----|
| `DEFAULT_URL`  | `jdbc:h2:~/frenemy;AUTO_SERVER=TRUE` |
| `DEFAULT_USER` | `sa` |
| `DEFAULT_PASS` | `""` |

| メソッド | 可視性 | 概要 |
|----------|-------|------|
| `getConnection()` | public static | 環境変数（なければデフォルト）で `DriverManager.getConnection(url, user, pass)` を返す |
| `getEnvOrDefault(name, defaultValue)` | private static | `System.getenv(name)` が `null` ならデフォルトを返す小ヘルパー |

> 全DAOはこの `DbConfig.getConnection()` だけを使って接続を取る。
> 接続先の切り替えは Eclipse の Run Configuration → Environment から環境変数を設定するだけで済む（コードは触らない）。

---

## `QuestionDAO.java`

**役割**: `question` / `answer_select` / `personality` テーブルを読んで、出題リストと選択肢を返す。

### クラス `QuestionDAO`

| 定数 | 概要 |
|------|------|
| `QUESTIONS_PER_PERSONALITY = 5` | 各特性から取り出す通常質問の数 |
| `SELECT_COLUMNS` | `SELECT question_id, question, critical, type, personality_id FROM question ` の共通部分 |

| メソッド | 可視性 | 戻り値 | 概要 |
|----------|-------|--------|------|
| `findQuiz()` | public | `List<Question>` | 出題リストを作る。<br>① `personality` から特性ID一覧を取得<br>② 各特性から `type='NORMAL'` の質問をランダム5問ずつ<br>③ `type <> 'NORMAL' AND critical = FALSE` の特殊質問（FAKE/FRENEMY）を全部<br>④ `critical = TRUE` の吉嵜質問を1問<br>⑤ `Collections.shuffle` で並び替え<br>失敗時は空リストを返す |
| `findChoices()` | public | `List<Choice>` | `answer_select` を `choice_id` 順に全件返す。失敗時は空リスト |
| `addQuestions(pStmt, list)` | private | `void` | 準備済み `PreparedStatement` を実行して結果を `Question` に詰めて `list` に追加するだけの小ヘルパー |

> ランダム抽選は `ORDER BY RAND() LIMIT ?` で DB 側に任せている（アプリ側でシャッフルしていない）。
> ドライバロードは `DbConfig` の static ブロック任せ。

---

## `CounterDAO.java`

**役割**: `result` テーブルの行数（＝これまでに診断を受けた人数）を数える。
`StartServlet` が診断開始画面に表示する人数カウンターのために呼ぶ。

### クラス `CounterDAO`

| メソッド | 可視性 | 戻り値 | 概要 |
|----------|-------|--------|------|
| `Counter()` | public | `int` | `SELECT COUNT(*) FROM result` を実行し、件数を返す。失敗時は `-1` |

> **命名規約**: 本来 Java のメソッド名は小文字始まり（`count()`）。既存のまま揃えているが、`StartServlet` の呼び出し箇所と合わせて将来リネームしてもいい。

---

## 削除済み: `template.java`

元々 DAO のJDBC接続情報の雛形としてコメントアウトされていたファイル。
`DbConfig` ができたことで役割が終わったので削除済み。
