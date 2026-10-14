# 全体像

## パッケージ構成

```
src/main/java/
├─ HelloWorld.java        動作確認用のサーブレット（本機能ではない）
├─ servlet/               画面の入口（HttpServlet）
│   ├─ TopServlet         "/"      トップページ
│   ├─ StartServlet       "/StartServlet" 診断開始画面（人数カウンター表示）
│   ├─ QuizServlet        "/quiz"  診断（出題・回答）
│   ├─ ResultServlet      "/result" 結果画面（仮）
│   ├─ CriticalServlet    "/critical" クリティカル吉嵜 演出
│   └─ Login              "/Login" ログイン（仮実装）
├─ dao/                   DBアクセス
│   ├─ DbConfig           JDBC接続の共通設定（環境変数読み込み）
│   ├─ QuestionDAO        questionテーブル・answer_selectテーブル参照
│   └─ CounterDAO         resultテーブルの行数（＝診断人数）
└─ model/                 データクラス
    ├─ User, LoginLogic   ログイン
    ├─ Question           questionテーブルの1行
    ├─ Choice             answer_selectテーブルの1行
    └─ Answer             回答1件（セッションに積む）

src/main/webapp/
├─ WEB-INF/jsp/           ビュー（JSP）
│   ├─ common/header.jsp  共通ヘッダー
│   ├─ top.jsp            トップ
│   ├─ start.jsp          診断開始
│   ├─ quiz.jsp           診断中
│   ├─ critical.jsp       クリティカル吉嵜
│   ├─ result.jsp         結果（仮）
│   ├─ error.jsp          汎用エラー
│   ├─ login.jsp          ログイン入力
│   └─ loginResult.jsp    ログイン結果
├─ view/hello.jsp         動作確認用（本機能ではない）
├─ js/
│   ├─ header.js          ヘッダーのアカウントメニュー開閉
│   └─ explosion.js       クリティカル吉嵜の爆発演出
└─ image/frenemy-log.png  ロゴ画像

db/schema.sql             H2のスキーマ＋初期データ
```

## 画面遷移（通常ルート）

```
TopServlet (/)
    │
    │  ヘッダー等
    ▼
StartServlet (/StartServlet)      ← CounterDAO で人数を取得
    │
    │ スタートボタン (POST action=start)
    ▼
QuizServlet (/quiz)               ← QuestionDAO で出題リストを作成
    │ 回答を1問ずつ POST (action=answer)
    │
    │ 吉嵜質問で「とても当てはまる」(choice_id=1)
    │ を選ぶと ─────────► CriticalServlet (/critical) 爆発演出
    │
    │ 全問回答
    ▼
ResultServlet (/result)           ← 現状は回答数を表示するだけの仮実装
```

ログイン系（`Login` / `login.jsp` / `loginResult.jsp`）は現状、
固定パスワード `1234` を判定するだけのモック。診断ルートからは呼ばれません。

## 状態の持ち方

- **セッション** (`HttpSession`) — 診断中の進行情報
  - `quizList` : `List<Question>` 出題リスト（シャッフル済み）
  - `quizIndex` : `Integer` 今何問目か（0オリジン）
  - `answerList` : `List<Answer>` 今までの回答
  - `choices` : `List<Choice>` 4段階の選択肢
  - `critical` : `Boolean` 吉嵜発動済みフラグ
  - `loginUser` : `User` ログイン済みユーザー（モック）
- **リクエストスコープ** — 画面に渡す一回限りの値
  - `count` (人数), `question`, `number`, `total`, `progress`, `answerCount`, `errorMessage` など
- **DB** — 問題マスター・結果・アカウントなど

セッション操作は `QuizServlet.clearQuiz()` が唯一の初期化口。
他サーブレット（`ResultServlet`・`CriticalServlet`）はセッションの有無で入り直しさせる。
