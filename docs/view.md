# `src/main/webapp/`

ビュー層（JSP）・JavaScript・画像。

- JSPはすべて `WEB-INF/jsp/` に置いて、URL直打ちで表示できないようにしている
- Tailwind CSS は WebJar から配信（`pom.xml` の `tailwindcss__browser`）
- JSTL（`c:` / `fmt:`）を使う。値の表示は `<c:out>` でエスケープする（XSS対策）
- 各画面は `<body class="flex min-h-screen flex-col ...">` ＋ `<main class="flex-1 ...">` で、フッターを下に固定
- 共通部品は include で取り込む
  ```jsp
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
  ```

---

## `WEB-INF/jsp/common/header.jsp`

ロゴ・メニュー・アカウントアイコン。

| id | 内容 |
|----|------|
| `globalNav` | トップに戻る・役職一覧。スマホでは隠れていて、`menuButton` で開く。md(768px)以上では常に横並び |
| `menuButton` | ハンバーガーボタン（スマホのみ） |
| `accountButton` / `accountMenu` | アカウントアイコンとメニュー。ログイン中は名前・マイページ・ログアウト（`POST /logout`）、未ログインはログイン・新規登録 |

## `WEB-INF/jsp/common/footer.jsp`

著作権表示。

---

## 画面

| JSP | サーブレット | 内容 |
|-----|-------------|------|
| `start.jsp` | `StartServlet` | タイトル・人数カウンター（`${count}`、-1なら「―」）・スタート（`POST /quiz action=start`）・役職一覧・説明 |
| `quiz.jsp` | `QuizServlet` | 進捗（`${number} / ${total}`・`${progress}%`）・質問・4つの選択肢ボタン（`questionId` と `choiceId` だけ送る）・戻る（2問目から） |
| `critical.jsp` | `CriticalServlet` | 爆発演出（`js/explosion.js`）→ 吉嵜の画像と「結果を見る」 |
| `result.jsp` | `ResultServlet` | 役職画像・「あなたの役職は〜です」（黒の役職は「お前は〜だ」＋赤黒の背景）・説明・スコアのバー・おすすめアイテム・画像保存・Xで共有・もう一度診断・トップへ・役職一覧・診断人数 |
| `roles.jsp` | `RolesServlet` | 15役職の画像・名前・説明。吉嵜は解放するまで「？？？」 |
| `login.jsp` | `Login` | ユーザー名・パスワード。エラー表示あり |
| `register.jsp` | `RegisterServlet` | 新規登録。エラー表示あり |
| `mypage.jsp` | `MyPageServlet` | ユーザー名と診断履歴（クリックで結果画面） |
| `error.jsp` | 各サーブレット | `${errorMessage}` とトップに戻るボタン |
| `top.jsp` | （未使用） | トップは開始画面にリダイレクトするようにしたので、今は使っていない |

---

## `js/header.js`

`setupToggle(button, menu, isOpen, setOpen)` で、アカウントメニューとハンバーガーメニューの開閉を同じ処理で行う。
ボタンで開閉、メニューの外をクリック or Esc キーで閉じる。`aria-expanded` も切り替える。

## `js/explosion.js`

クリティカル吉嵜画面の演出。火花220個＋衝撃波＋画面の揺れとフラッシュ → 1.2秒後に `#reveal` を表示。
`prefers-reduced-motion` が on なら演出なしですぐ表示。

## `js/result-image.js`

結果画面の「画像にして保存する」。`#resultCard` の `data-*`（見出し・役職名・画像・アイテム・黒かどうか）と
`#scoreList li` の `data-name` / `data-score` を読んで canvas に描き、PNG でダウンロードする。
（html2canvas は Tailwind v4 の色指定 `oklch()` を読めずに失敗するので使っていない）

---

## 画像

- `image/frenemy-log.png` : ロゴ
- `image/role/*.png` : 役職画像（15枚）。ファイル名は英語名（`smn.png` = 市民 など）で、役職IDとの対応は `db/schema.sql` の `INSERT INTO image` にある。
  差し替えるときはファイルを置き換えるか、DBの `image.path` を変える。
