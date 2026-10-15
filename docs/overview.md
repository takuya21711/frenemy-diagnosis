# 全体像

## パッケージ構成

```
src/main/java/
├─ HelloWorld.java        動作確認用のサーブレット（本機能ではない）
├─ servlet/               画面の入口（HttpServlet）＋ サーブレット層ヘルパー
│   ├─ TopServlet         "/"            → /StartServlet へリダイレクト
│   ├─ StartServlet       "/StartServlet" 診断開始画面（人数カウンター表示）
│   ├─ QuizServlet        "/quiz"        診断（出題・回答・戻る・結果の保存）
│   ├─ ResultServlet      "/result?id="  結果画面（DBから読んで表示するだけ）
│   ├─ CriticalServlet    "/critical?id=" クリティカル吉嵜 演出
│   ├─ RolesServlet       "/roles"       役職一覧（吉嵜は解放するまで ？？？）
│   ├─ Login              "/Login"       ログイン
│   ├─ RegisterServlet    "/register"    新規登録
│   ├─ LogoutServlet      "/logout"      ログアウト（POST）
│   ├─ MyPageServlet      "/mypage"      マイページ（診断履歴）
│   ├─ YoshizakiUnlock    吉嵜の解放判定（Cookie＋アカウント）
│   ├─ SessionKeys        HttpSession 属性キーの定数置き場
│   ├─ LoginSession       ログインユーザー取得ヘルパー
│   ├─ QuizSession        診断中のセッション状態ラッパー
│   └─ WebUtils           エラー画面へのフォワード共通化
├─ dao/                   DBアクセス
│   ├─ DbConfig           JDBC接続の共通設定（環境変数読み込み）
│   ├─ QuestionDAO        出題リスト・選択肢
│   ├─ ScoringDAO         質問×選択肢の点数・フレネミー固定スコア
│   ├─ RoleDAO            役職＋閾値
│   ├─ ResultDAO          結果の保存・取得・履歴
│   ├─ AccountDAO         アカウント
│   └─ CounterDAO         resultテーブルの行数（＝診断人数）
└─ model/
    ├─ Question, Choice, Answer   出題・回答
    ├─ Scores, ScoreCalculator    スコア計算（素点 → min-max正規化）
    ├─ Role, Threshold, RoleJudge 役職判定
    ├─ Result, TraitScore         結果画面の表示用
    └─ User, Account, LoginLogic, PasswordUtil  ログイン

src/main/webapp/
├─ WEB-INF/jsp/           ビュー（JSP）
│   ├─ common/header.jsp  共通ヘッダー（ハンバーガーメニュー・アカウントメニュー）
│   ├─ common/footer.jsp  共通フッター（著作権表示）
│   ├─ start.jsp / quiz.jsp / critical.jsp / result.jsp / roles.jsp
│   ├─ login.jsp / register.jsp / mypage.jsp / error.jsp
│   └─ top.jsp            （未使用。トップは開始画面にリダイレクト）
├─ js/
│   ├─ header.js          ヘッダーのメニュー開閉
│   ├─ explosion.js       クリティカル吉嵜の爆発演出
│   └─ result-image.js    結果を画像(PNG)にして保存
└─ image/
    ├─ frenemy-log.png    ロゴ
    └─ role/1.svg〜15.svg 役職の仮画像

db/schema.sql             H2のスキーマ＋初期データ
```

## 画面遷移

```
TopServlet (/) ──redirect──► StartServlet (/StartServlet)   ← CounterDAO で人数
                                 │ スタート (POST /quiz action=start)
                                 ▼
                             QuizServlet (/quiz)            ← QuestionDAO で出題リスト
                                 │ 回答 (action=answer) / 戻る (action=back)
                                 │
                                 │ 吉嵜質問で「とても当てはまる」
                                 │   → 吉嵜として result に保存・Cookie で解放
                                 │   ─────► CriticalServlet (/critical?id=) 爆発 ─► 結果画面
                                 │
                                 │ 全問回答
                                 │   → ScoreCalculator → RoleJudge → ResultDAO.insert
                                 ▼
                             ResultServlet (/result?id=)    ← ResultDAO.findById

ヘッダーから: 役職一覧 (/roles)、ログイン (/Login)、新規登録 (/register)、
             マイページ (/mypage)、ログアウト (POST /logout)
```

## スコア計算と役職判定

1. 回答（質問ID・選択肢ID）だけをセッションに持つ。点数は画面から受け取らない
2. 全問答えたら、`scoring` テーブルの点数を特性ごとに合計（素点）。フェイク・フレネミー・吉嵜質問は数えない
3. min-max正規化で -30〜+30 にそろえる：`(素点 - 最小) / (最大 - 最小) × 60 - 30`
   最小・最大は「出題された質問の選択肢の最低点・最高点」の合計（5問なら -25〜+25）
4. 「自分をフレネミーだと思いますか」で「とても当てはまる」なら、`fixed_score` の値で5特性を上書き
5. 各役職について、閾値（`role_threshold`）に入っている特性の数を数え、一番多い役職にする。同率ならランダム
6. `result` に保存して `/result?id=結果ID` へリダイレクト。結果画面はIDで読むだけなので、リロードしても二重に保存されない

## 状態の持ち方

- **セッション** (`HttpSession`)
  - `quizList` : `List<Question>` 出題リスト（シャッフル済み）
  - `quizIndex` : `Integer` 今何問目か（0オリジン）
  - `answerList` : `List<Answer>` 今までの回答
  - `choices` : `List<Choice>` 4段階の選択肢
  - `loginUser` : `User` ログイン中のユーザー（パスワードは持たない）
- **Cookie**
  - `yoshizaki=1` : このブラウザで吉嵜を出した（役職一覧で解放）。1年間有効
- **リクエストスコープ** — 画面に渡す一回限りの値（`count`, `question`, `result`, `shareUrl`, `errorMessage` など）
- **DB** — 質問・役職・閾値・結果・アカウント

診断中のセッション情報は `QuizServlet.clearQuiz()` で消す（開始時・結果保存後・クリティカル時）。
