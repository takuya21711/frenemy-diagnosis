# `src/main/java/servlet/`

画面の入口（`HttpServlet` のサブクラス）。URLは全部 `@WebServlet` アノテーションで宣言しています。
POST のあとは必ずリダイレクトする（PRGパターン）ので、リロードで二重送信になりません。

---

## `TopServlet.java`

**URL** : `""`（＝ `http://localhost:8080/frenemy-diagnosis/`）
**役割**: トップ＝診断開始画面なので、`/StartServlet` にリダイレクトするだけ。

---

## `StartServlet.java`

**URL** : `/StartServlet`
**役割**: 診断開始画面。これまでに診断を受けた人数（＝`result` テーブルの行数）を表示する。

| メソッド | 概要 |
|----------|------|
| `doGet` | `CounterDAO.Counter()` で人数を取得し `count` に入れて `start.jsp` へフォワード |
| `doPost` | `doGet` に委譲 |

---

## `QuizServlet.java`

**URL** : `/quiz`
**役割**: 診断の中核。出題・回答受け取り・戻る・吉嵜発動・結果の保存。

### 定数・セッションキー

| 名前 | 役割 |
|------|------|
| `CRITICAL_CHOICE_ID = 1` | 「とても当てはまる」の choice_id。吉嵜質問でこれを選ぶとクリティカル |
| `QUIZ_LIST`   | セッション：`List<Question>` 出題リスト |
| `QUIZ_INDEX`  | セッション：`Integer` 今何問目か（0オリジン） |
| `ANSWER_LIST` | セッション：`List<Answer>` 回答履歴 |
| `CHOICES`     | セッション：`List<Choice>` 4段階の選択肢 |

### メソッド

| メソッド | 概要 |
|----------|------|
| `doGet` | 診断中でなければ `StartServlet` へ。今の問題・番号・進捗％をセットして `quiz.jsp` へ |
| `doPost` | `action` が `start` / `answer` / `back` で振り分け |
| `start` | `clearQuiz()` → `QuestionDAO` で出題リストを作ってセッションへ → `/quiz` にリダイレクト |
| `answer` | `questionId` が「今の問題」と一致し、選択肢が正しいときだけ受け付ける。<br>・吉嵜質問 × とても当てはまる → 吉嵜(15)として保存、Cookie で解放、`/critical?id=` へ<br>・最後の問題 → `saveResult()` して `/result?id=` へ<br>・それ以外 → 次の問題へ<br>連打対策で `synchronized (session)` の中で処理する |
| `back` | 直前の回答を取り消して1問戻る |
| `saveResult` | `ScoringDAO` → `ScoreCalculator` → `RoleDAO` + `RoleJudge` → `ResultDAO.insert`。失敗したら -1 |
| `clearQuiz(session)` | 診断中のセッション情報を消す |

---

## `ResultServlet.java`

**URL** : `/result?id=結果ID`
**役割**: 結果画面。保存は `QuizServlet` が済ませているので、DB から読んで表示するだけ。このURLをXで共有する。

| メソッド | 概要 |
|----------|------|
| `doGet` | `ResultDAO.findById` で結果を読み、`result`・`shareUrl`（Xの投稿画面URL）・`count` をセットして `result.jsp` へ。見つからなければ 404 で `error.jsp` |
| `findResult(request)` | `?id=` を読んで結果を返す（`CriticalServlet` も使う）。不正なら null |

---

## `CriticalServlet.java`

**URL** : `/critical?id=結果ID`
**役割**: クリティカル吉嵜の演出画面。`id` が吉嵜の結果でなければ `StartServlet` へ戻す。

---

## `RolesServlet.java`

**URL** : `/roles`
**役割**: 役職一覧。`RoleDAO.findAll()` と `YoshizakiUnlock.isUnlocked()` を JSP に渡す。

---

## `YoshizakiUnlock.java`

**役割**: 役職一覧の「吉嵜」を解放したかどうかの判定。

| メソッド | 概要 |
|----------|------|
| `unlock(request, response)` | Cookie `yoshizaki=1`（1年・HttpOnly）を付ける。吉嵜が出たときに `QuizServlet` が呼ぶ |
| `isUnlocked(request)` | Cookie がある、またはログイン中のアカウントで吉嵜を出したことがある（`ResultDAO.hasRole`）なら true |

---

## `Login.java`

**URL** : `/Login`

| メソッド | 概要 |
|----------|------|
| `doGet` | `login.jsp` へ |
| `doPost` | `LoginLogic.login()` で確認。失敗したらエラーを出して `login.jsp` へ、成功したら `loginAs()` して `/StartServlet` へリダイレクト |
| `loginAs(request, user)` | セッションIDを変えて（`changeSessionId()`、セッション固定攻撃の対策）`loginUser` に入れる |

定数 `LOGIN_USER = "loginUser"` がセッションのキー。

---

## `RegisterServlet.java`

**URL** : `/register`
GET でフォーム、POST で `LoginLogic.register()`。成功したらそのままログインして `/StartServlet` へ。入力ミスや名前の重複はフォームにエラー表示。

---

## `LogoutServlet.java`

**URL** : `/logout`（POSTのみ）
セッションを `invalidate()` して `/StartServlet` へ。

---

## `MyPageServlet.java`

**URL** : `/mypage`
未ログインなら `/Login` へ。ログイン中なら `ResultDAO.findByUser` の診断履歴を `mypage.jsp` に渡す。

---

## `HelloWorld.java`

Eclipse の WTP が生成した動作確認用サーブレット。本機能ではない。

---

## ヘルパー類（サーブレット層の共通処理）

サーブレット間で重複していた処理を 1 箇所に閉じるために導入した小さなクラス群。

### `SessionKeys.java`

`HttpSession` 属性のキー名（`loginUser` / `quizList` / `quizIndex` / `answerList` / `choices`）を集めた定数置き場。
文字列を直接書くとタイポに気付けないので、必ずここ経由で参照する。

### `LoginSession.java`

| メソッド | 概要 |
|----------|------|
| `currentUser(request)` | ログイン中のユーザー。未ログイン（セッション無し含む）なら `null` |
| `currentUserId(request)` | 同上の `userId` だけ。DAO に `Integer userId` を渡す用 |

従来 `(User) session.getAttribute(Login.LOGIN_USER)` を書いていた 3 箇所をこのヘルパーに置き換えてある。

### `QuizSession.java`

診断中のセッション状態をラップする薄いクラス。`QuizServlet` から
`(List<Question>) session.getAttribute(...)` のキャストと `@SuppressWarnings("unchecked")` を追い出すのが目的。

| メソッド | 概要 |
|----------|------|
| `QuizSession.of(request)` | 既存セッションが無ければ `null` |
| `QuizSession.create(request)` | 新規または既存セッションを取得（`start` 用） |
| `raw()` | 元の `HttpSession`（連打対策の `synchronized` 用モニタ） |
| `isStarted()` | 診断中かどうか（`quizList` が乗っているか） |
| `getQuizList()` / `getAnswerList()` / `getChoices()` / `getIndex()` | 現在の状態の読み出し |
| `currentQuestion()` | 今の問題（インデックスで引く） |
| `start(quizList, choices)` | 出題リストと選択肢をセット、インデックス 0、回答空リスト |
| `addAnswer(answer)` | 回答追加 ＋ インデックス前進 |
| `undoLastAnswer()` | 直前の回答を取り消し、インデックスを 1 戻す（戻るボタン用） |
| `removeLastAnswer()` | インデックスは動かさず直前の回答だけ取り消す（結果保存失敗時のリトライ用） |
| `clear()` | 診断中の属性を全部削除 |

### `WebUtils.java`

エラー画面へのフォワードをまとめたユーティリティ。

| メソッド | 概要 |
|----------|------|
| `forwardError(request, response, message)` | `errorMessage` をリクエストスコープに入れて `/WEB-INF/jsp/error.jsp` にフォワード |
| `forwardError(request, response, message, status)` | HTTP ステータス指定版（404 など） |
