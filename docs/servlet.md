# `src/main/java/servlet/`

画面の入口（`HttpServlet` のサブクラス）。URLは全部 `@WebServlet` アノテーションで宣言しています。

---

## `TopServlet.java`

**URL** : `""`（＝ `http://localhost:8080/frenemy-diagnosis/`）
**役割**: トップページを表示するだけ。

### クラス `TopServlet extends HttpServlet`

| メソッド | 概要 |
|----------|------|
| `doGet(request, response)` | `/WEB-INF/jsp/top.jsp` へフォワード |

---

## `StartServlet.java`

**URL** : `/StartServlet`
**役割**: 診断開始画面。これまでに診断を受けた人数（＝`result` テーブルの行数）を表示する。

### クラス `StartServlet extends HttpServlet`

| メソッド | 概要 |
|----------|------|
| `doGet(request, response)` | `CounterDAO.Counter()` で人数を取得し、`count` としてリクエストスコープに入れて `start.jsp` へフォワード |
| `doPost(request, response)` | `doGet` に委譲（POSTでも同じ画面を出す） |

---

## `QuizServlet.java`

**URL** : `/quiz`
**役割**: 診断の中核。出題・回答受け取り・進捗・吉嵜発動を扱う。

### 定数・セッションキー

| 名前 | 役割 |
|------|------|
| `CRITICAL_CHOICE_ID = 1` | 「とても当てはまる」のchoice_id。吉嵜質問でこれを選ぶとクリティカル |
| `QUIZ_LIST`   | セッション：`List<Question>` 出題リスト |
| `QUIZ_INDEX`  | セッション：`Integer` 今何問目か（0オリジン） |
| `ANSWER_LIST` | セッション：`List<Answer>` 回答履歴 |
| `CHOICES`     | セッション：`List<Choice>` 4段階の選択肢 |
| `CRITICAL`    | セッション：`Boolean` 吉嵜発動済み（`CriticalServlet` が読む） |

### クラス `QuizServlet extends HttpServlet`

| メソッド | 可視性 | 概要 |
|----------|-------|------|
| `doGet(request, response)` | protected | セッションに `quizList` がなければ `StartServlet` にリダイレクト。全問終わっていれば `/result` へ。それ以外は今の問題・問題番号・進捗％を属性にセットして `quiz.jsp` にフォワード |
| `doPost(request, response)` | protected | パラメータ `action` を見て `start` / `answer` に振り分け。それ以外は `/quiz` にリダイレクト |
| `start(request, response)`  | private | 前回の診断情報を `clearQuiz()` で消し、`QuestionDAO` から新しい出題リストと選択肢を取り、セッションに保存。失敗したら `error.jsp` に遷移。成功時はPRGパターンで `/quiz` にリダイレクト |
| `answer(request, response)` | private | `questionId` と `choiceId` を読む。今の問題と `questionId` が一致していて選択肢が妥当なときだけ受け付ける。吉嵜質問 × choiceId=1 なら `clearQuiz` して `/critical` へ。それ以外は回答を積んでインデックスを進め、終わっていれば `/result`、まだなら `/quiz` にリダイレクト |
| `isValidChoice(choices, choiceId)` | private | `choices` のどれかに `choiceId` が一致するか |
| `clearQuiz(session)`        | package-private static | セッションから `QUIZ_LIST` / `QUIZ_INDEX` / `ANSWER_LIST` / `CHOICES` を削除。`CriticalServlet` などから流れを初期化するときにも使える |

> `answer()` は不正入力（戻るボタン・二重送信・改変パラメータ）に強い作りにしてある：index と `questionId` の一致チェック・`NumberFormatException` の握りつぶし・PRGパターンでのリダイレクト。

---

## `ResultServlet.java`

**URL** : `/result`
**役割**: 診断結果画面。**現状は仮実装**で、回答数だけ表示する。

### クラス `ResultServlet extends HttpServlet`

| メソッド | 概要 |
|----------|------|
| `doGet(request, response)` | セッションの `ANSWER_LIST` が無ければ `StartServlet` にリダイレクト。あれば回答数を `answerCount` にセットして `result.jsp` へフォワード |

**TODO（コメントで宣言済み）**: スコア計算 → 役職判定 → `result` テーブルに保存 → `result?id=結果ID` にリダイレクト。

---

## `CriticalServlet.java`

**URL** : `/critical`
**役割**: 吉嵜質問で「とても当てはまる」を選んだ人用の演出画面（爆発 → 吉嵜露出）。

### クラス `CriticalServlet extends HttpServlet`

| メソッド | 概要 |
|----------|------|
| `doGet(request, response)` | セッションの `CRITICAL` フラグが `true` でなければ `StartServlet` にリダイレクト。TRUEなら `critical.jsp` にフォワード |

**TODO**: `result` テーブルに吉嵜（role_id=15）として保存し、役職一覧で吉嵜を解放する。

---

## `Login.java`

**URL** : `/Login`
**役割**: ログインの仮実装（固定パスワード `"1234"` を判定するだけ）。診断本線からは呼ばれない。

### クラス `Login extends HttpServlet`

| メソッド | 概要 |
|----------|------|
| `doGet(request, response)` | `login.jsp` にフォワード |
| `doPost(request, response)` | リクエストから `name` / `pass` を取得 → `User` を作成 → `LoginLogic.execute()` で判定 → 成功ならセッションに `loginUser` を入れる → `loginResult.jsp` にフォワード |

---

## `HelloWorld.java`

Eclipse の WTP が生成した動作確認用サーブレット。本機能ではない。
`/HelloWorld` に GET すると `"Served at: /frenemy-diagnosis"` を返すだけ。
