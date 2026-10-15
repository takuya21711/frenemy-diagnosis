# `src/main/java/model/`

データクラスと、DBに触らない計算ロジック。
セッションに入るものは `Serializable` にしてある。

---

## 出題・回答

| クラス | 役割 |
|--------|------|
| `Question` | `question` テーブルの1行。`questionId` / `question` / `critical` / `type`（NORMAL/FAKE/FRENEMY）/ `personalityId`（特殊質問は 0） |
| `Choice` | `answer_select` の1行。`choiceId`（1=とても … 4=当てはまらない）/ `label` |
| `Answer` | 1問分の回答（`questionId` と `choiceId` だけ）。**点数は持たない**。スコア計算時にDBから引く |

---

## スコア計算

### `Scores`

5特性の正規化スコア（-30〜+30）。添字＝特性ID（1=外向性 2=協調性 3=誠実性 4=情緒安定性 5=開放性）。
`get(id)` / `set(id, score)` と、`getExtraversion()` などの名前つきゲッター。

### `ScoreCalculator`

| メソッド | 概要 |
|----------|------|
| `calculate(questions, answers, scoring, fixedScores)` | ① 通常質問の点数を特性ごとに合計（素点）<br>② min-max正規化で -30〜+30 に<br>③ `fixedScores` があればその特性を上書き |
| `normalize(raw, min, max)` | `(raw - min) / (max - min) × 60 - 30` を四捨五入。質問が無い特性は 0 |

素点の最小・最大は「答えた質問ごとの、選択肢の最低点・最高点」の合計なので、問題数や点数を DB で変えてもそのまま動く。

---

## 役職判定

| クラス | 役割 |
|--------|------|
| `Role` | `role` の1行 ＋ `List<Threshold>` ＋ 一覧用の `imagePath`。`black` で黒の役職。`YOSHIZAKI_ID = 15`、`isYoshizaki()` |
| `Threshold` | 役職×特性の範囲 `min`〜`max`。`contains(score)` |
| `RoleJudge` | `judge(roles, scores)`：閾値に入っている特性の数（`countMatches`）が一番多い役職を返す。同率はランダム。閾値の無い役職（吉嵜）は対象外。テスト用に `Random` を渡せるコンストラクタもある |

---

## 結果画面

| クラス | 役割 |
|--------|------|
| `Result` | 結果1件：`resultId` / `role` / `traits`（`List<TraitScore>`、吉嵜のときは空）/ `imagePath` / `itemName` / `createdAt` |
| `TraitScore` | 特性名とスコア。`getPercent()` でバーの幅（0〜100） |

---

## ログイン

| クラス | 役割 |
|--------|------|
| `User` | セッションの `loginUser`。`userId` と `name` だけ（パスワードは持たせない） |
| `Account` | `account` テーブルの1行（ハッシュ化したパスワードを含む）。ログイン判定にだけ使う |
| `LoginLogic` | `login(name, pass)`：合っていれば `User`、違えば null。<br>`register(name, pass)`：入力チェック（名前1〜50文字・パスワード4文字以上）と重複チェックをして登録。ダメなときは `IllegalArgumentException`（メッセージをそのまま画面に出す） |
| `PasswordUtil` | `hash(pass)` / `verify(pass, stored)`。PBKDF2WithHmacSHA256（ソルト16バイト・10万回）。保存形式は `salt:hash`（Base64） |
