# `src/main/webapp/`

ビュー層（JSP）・JavaScript・画像・デプロイ記述。

- JSPはすべて `WEB-INF/jsp/` に置いて、URL直打ちで表示できないようにしている
- Tailwind CSS は WebJar から配信（`pom.xml` の `tailwindcss__browser`）
- 共通ヘッダーは `common/header.jsp` を各JSPから `<%@ include %>` で取り込む

---

## `WEB-INF/web.xml`

デプロイ記述子。`welcome-file-list` を書いているが、
URLは全部サーブレット側の `@WebServlet` で定義しているので、
実質ほぼ初期値のまま。`display-name` が `frenemy-diagnosis`。

---

## `WEB-INF/jsp/common/header.jsp`

**役割**: 全画面共通のヘッダー。ロゴ・トップに戻るリンク・アカウントアイコン＆メニュー。
各JSPから `<%@ include file="/WEB-INF/jsp/common/header.jsp" %>` で取り込む。

主要素：

| id | 内容 |
|----|------|
| `accountButton` | アカウントアイコン（丸いボタン）。クリックでメニュー開閉（`header.js`） |
| `accountMenu`   | ドロップダウン本体。ユーザー名表示とログアウトボタン |
| `logoutButton`  | ログアウト（**現状モック**。クリックで alert が出るだけ） |

**TODO**:
- ユーザー名は `session.loginUser` を参照するように差し替え
- ログアウトは `LogoutServlet` へ送るように差し替え

---

## `WEB-INF/jsp/top.jsp`

**役割**: トップページ（`TopServlet` からフォワード）。
ヘッダー + `<main>トップページ</main>` だけのプレースホルダ。

---

## `WEB-INF/jsp/start.jsp`

**役割**: 診断開始画面（`StartServlet` からフォワード）。

- `${count}` : これまでの診断人数（`CounterDAO.Counter()` 由来）
- スタートボタンは `POST /quiz` に `action=start` を送って診断を開始する
- 役職一覧ボタンは **現状未配線**（何も起きない）

---

## `WEB-INF/jsp/quiz.jsp`

**役割**: 診断中の1問を表示（`QuizServlet.doGet` からフォワード）。
JSTL (`c:forEach` / `c:out`) を使う（`<%@ taglib prefix="c" uri="jakarta.tags.core" %>`）。

リクエストスコープから読む値：

| 変数 | 中身 |
|------|------|
| `${number}` / `${total}` | 進捗（例：3 / 32 問） |
| `${progress}` | 0〜99の整数（％表示とプログレスバーの幅） |
| `${question}` | `Question`。`${question.question}` で設問文、`${question.questionId}` を hidden input に埋め込む |
| `${choices}` | `List<Choice>`。各選択肢を `name="choiceId" value="${choice.choiceId}"` のsubmitボタンとして出す |

フォームは `POST /quiz` に `action=answer` + `questionId` + `choiceId` を送る。

---

## `WEB-INF/jsp/critical.jsp`

**役割**: クリティカル吉嵜の演出（`CriticalServlet` からフォワード）。

- 背景色は焦げた赤茶 (`bg-[#1a0a00]`)
- `<canvas id="explosion">` と `<div id="flash">` を全画面に敷いて、`js/explosion.js` が爆発＋フラッシュを描画
- `<section id="reveal" hidden>` に吉嵜本体（現状は「吉嵜」の文字円）。`is-shown` クラスが付くとフェード＋ポンっと拡大
- `@keyframes shake` / `flash` は画面全体の揺れと白飛び
- 「もう一度診断する」ボタンは `POST /quiz` の `action=start`。「トップに戻る」は `/StartServlet` へ

**TODO**: 吉嵜の画像ができたら、丸いプレースホルダを差し替え。

---

## `WEB-INF/jsp/result.jsp`

**役割**: 結果画面（`ResultServlet` からフォワード）。**現状は仮**。
`${answerCount}` 問に回答しました、とだけ出して、もう一度ボタンを置く。

**TODO**: スコア計算・役職判定ができたら作り直し。

---

## `WEB-INF/jsp/error.jsp`

**役割**: 汎用エラー画面。
現状は `QuizServlet` が出題取得に失敗したときにだけ使われる。
`${errorMessage}` を表示し、「トップに戻る」ボタンを置く（default="予期しないエラーが発生しました。"）。

---

## `WEB-INF/jsp/login.jsp`

**役割**: ログイン入力画面（`Login.doGet` からフォワード）。
`name` と `pass` を `POST /Login` に送るだけ。

---

## `WEB-INF/jsp/loginResult.jsp`

**役割**: ログイン結果画面（`Login.doPost` からフォワード）。
`<%@page import="model.User" %>` でセッションから `loginUser` を取り、
null でなければ「ログイン成功」＋「フレネミー診断へ」のリンク、
null なら「失敗」＋ `login.jsp` へのリンクを出す。

> JSTL ではなくスクリプトレット (`<% ... %>`) を使っている古いスタイル。
> 他のJSPに合わせて書き直すのが望ましい。

---

## `view/hello.jsp`

Eclipse WTP が作る動作確認用の "hello world" JSP。本機能ではない。

---

## `js/header.js`

**役割**: ヘッダーの `accountButton` クリックでメニュー (`accountMenu`) を開閉。
メニュー外クリック or Escキーで閉じる。`logoutButton` クリックは alert 出すだけ（モック）。

主要変数：
- `button` : アカウントアイコン
- `menu`   : ドロップダウン本体
- `logoutButton` : ログアウトボタン

関数：
- `closeMenu()` : `menu.hidden = true` と `aria-expanded="false"`

---

## `js/explosion.js`

**役割**: クリティカル吉嵜画面の演出。`critical.jsp` から defer 読み込み。

主な動き：
1. `prefers-reduced-motion: reduce` が on、または canvas が使えない環境では即座に吉嵜を表示して終了
2. `<canvas>` の DPR 対応リサイズ
3. 220個の火花パーティクルを中央から全方向に射出（重力・減衰あり）
4. 衝撃波の輪 (`shockwave`) を広げる
5. `body` に `shake` クラス、`#flash` に `flash` クラスを付けて揺れとフラッシュ
6. 1.2秒後に `#reveal` を見せ、`is-shown` クラスでフェードイン

パーティクル1個の形：

```js
{ x, y, vx, vy, size, life, decay, color }
```

関数：
- `showYoshizaki()` : `reveal.hidden` を外して次フレームで `is-shown` を付与
- `resize()` : canvas を DPR 込みでウィンドウサイズに合わせる
- `draw()` : 1フレーム描画 → `requestAnimationFrame(draw)` で継続。全パーティクルが死んだら canvas を remove

---

## `image/frenemy-log.png`

ヘッダー左のロゴ画像（`common/header.jsp` が参照）。
