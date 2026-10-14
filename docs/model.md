# `src/main/java/model/`

データクラス（DTO）＋ ログインロジック。
すべて `Serializable` にしてある（セッションに入れるため）。

---

## `Question.java`

**役割**: `question` テーブルの1行を表す。`QuestionDAO` が返し、`QuizServlet` がセッションに積む。

### クラス `Question implements Serializable`

| フィールド | 型 | 由来・意味 |
|-----------|----|-----------|
| `questionId` | `int` | `question_id` |
| `question`   | `String` | 設問文 |
| `critical`   | `boolean` | TRUE なら「とても当てはまる」でクリティカル吉嵜発動 |
| `type`       | `String` | `NORMAL` / `FAKE` / `FRENEMY` |
| `personalityId` | `int` | 特性ID。特殊質問はDBだと NULL だが、ここでは 0 として入ってくる（`ResultSet.getInt` は NULL を 0 として返すため） |

| メソッド | 概要 |
|----------|------|
| `Question()` | 既定コンストラクタ |
| `Question(questionId, question, critical, type, personalityId)` | 5引数コンストラクタ |
| `getQuestionId() / getQuestion() / isCritical() / getType() / getPersonalityId()` | ゲッター |

---

## `Choice.java`

**役割**: `answer_select` テーブルの1行（回答の選択肢）。
`QuestionDAO.findChoices()` が返し、`QuizServlet` がセッションに積む。

### クラス `Choice implements Serializable`

| フィールド | 型 | 意味 |
|-----------|----|------|
| `choiceId` | `int` | 1=とても当てはまる / 2=まあ当てはまる / 3=あまり / 4=当てはまらない |
| `label`    | `String` | 画面に出す文言 |

| メソッド | 概要 |
|----------|------|
| `Choice()` / `Choice(choiceId, label)` | コンストラクタ |
| `getChoiceId() / getLabel()` | ゲッター |

---

## `Answer.java`

**役割**: 1問分の回答。セッションの `answerList` に積み上がっていく。
**点数は持たない**（スコア計算時にDBの `scoring` テーブルから引く設計）。

### クラス `Answer implements Serializable`

| フィールド | 型 | 意味 |
|-----------|----|------|
| `questionId` | `int` | 回答した問題のID |
| `choiceId`   | `int` | 選んだ選択肢ID |

| メソッド | 概要 |
|----------|------|
| `Answer()` / `Answer(questionId, choiceId)` | コンストラクタ |
| `getQuestionId() / getChoiceId()` | ゲッター |

---

## `User.java`

**役割**: ログインユーザー。現状は `name` と `pass` を持つだけのモック。
将来の `account` テーブル連携時に、`user_id` などを足していく想定。

### クラス `User implements Serializable`

| フィールド | 型 |
|-----------|----|
| `name` | `String` |
| `pass` | `String` |

| メソッド | 概要 |
|----------|------|
| `User()` / `User(name, pass)` | コンストラクタ |
| `getName() / getPass()` | ゲッター（セッターは無い） |

---

## `LoginLogic.java`

**役割**: 認証ロジック。**現状は固定パスワード `"1234"` を判定するだけのモック**。

### クラス `LoginLogic`

| メソッド | 戻り値 | 概要 |
|----------|-------|------|
| `execute(User user)` | `boolean` | `user.getPass().equals("1234")` なら true |

> ここに `account` テーブル参照・パスワードハッシュ照合を実装していく予定。
