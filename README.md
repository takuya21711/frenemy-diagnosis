# frenemy-diagnosis

フレネミー診断の Jakarta EE (Servlet / JSP) アプリです。
Eclipse + Tomcat + H2 Database で動かす前提で書いています（Docker は使いません）。

---

## 1. 必要なもの

| ツール | バージョン | メモ |
|--------|-----------|------|
| JDK | **21**（JavaSE-21） | `pom.xml` の `maven.compiler.release` が 21 |
| Eclipse | 2024-06 以降の **Enterprise Java and Web Developers** 版 | Jakarta EE / WTP / m2e が入っているもの |
| Apache Tomcat | **11.x** | Jakarta Servlet 6.1 (`jakarta.*`) を使うので Tomcat 10 以降、11 推奨 |
| H2 Database | 2.3.x | DAO が TCP モードで接続するので、H2 サーバーが別途起動している必要があります |

> Jakarta EE 系（`jakarta.servlet.*`）を使っているので、**Tomcat 9 ではビルドは通っても動きません**。必ず Tomcat 10 以降（できれば 11）を入れてください。

---

## 2. 環境構築（Eclipse でのインポート）

1. 任意のフォルダに clone します。
   ```
   git clone <このリポジトリのURL> frenemy-diagnosis
   ```
2. Eclipse を起動 →  
   `File` → `Import...` → `Maven` → `Existing Maven Projects` を選択。
3. Root Directory に clone したフォルダを指定して `Finish`。
   - m2e が `pom.xml` を読んで、依存ライブラリを自動で取得します。
4. プロジェクトを右クリック → `Maven` → `Update Project...`（`Alt + F5`）を一度実行しておくと確実です。
5. Tomcat 11 を Eclipse に登録します。
   - `Window` → `Preferences` → `Server` → `Runtime Environments` → `Add...`
   - Apache Tomcat v11.0 を選び、インストール済み Tomcat のフォルダを指定。
6. プロジェクトを右クリック → `Run As` → `Run on Server` → 先ほど登録した Tomcat を選択。
7. ブラウザで次の URL を開いて動作確認してください。
   ```
   http://localhost:8080/frenemy-diagnosis/
   ```

### うまく動かない時のチェック

- `jakarta.servlet` が解決できない → Tomcat が 10 未満。11 を入れ直す。
- `java.lang.UnsupportedClassVersionError` → Eclipse の JRE が 21 になっているか確認（`Project` → `Properties` → `Java Build Path` → `Libraries`）。
- 404 が返る → Context root が `frenemy-diagnosis` になっているか、サーバーの `Modules` を確認。

---

## 3. DB（H2）の接続方法

このアプリは H2 Database に接続します。
接続先は **環境変数で切り替え** られるので、学校の共用H2・自PCのローカルH2のどちらでも、コードを書き換えずに動かせます。

### 3-1. 接続情報（環境変数）

接続情報は `src/main/java/dao/DbConfig.java` が以下の環境変数から読みます。
未設定の場合は「自PCのローカルファイルモードH2」にフォールバックします（＝ 家で何も設定しなくても最低限動く）。

| 環境変数 | 役割 | 未設定時のデフォルト |
|----------|------|----------------------|
| `DB_URL`  | JDBC URL   | `jdbc:h2:~/frenemy;AUTO_SERVER=TRUE` |
| `DB_USER` | ユーザー   | `sa` |
| `DB_PASS` | パスワード | （空） |

利用シーン別の設定例：

| 環境 | `DB_URL` に入れる値 |
|------|---------------------|
| 学校（共用H2サーバーに繋ぐ） | `jdbc:h2:tcp://10.23.104.249/~/frenemy` ※IPは当日の担当者に合わせる |
| 家（自PCのファイルモード・デフォルトでOK） | 未設定（＝デフォルトが使われる） |
| 家（自PCでTCPサーバーを立てて繋ぐ）       | `jdbc:h2:tcp://localhost/~/frenemy` |

> `10.23.104.249` は **DBを立てている人のPCのローカルIP** です。固定値ではないので、学校で動かすときは当日の担当者のIPを `DB_URL` にセットしてください。

ドライバは `org.h2.Driver`（`pom.xml` の `com.h2database:h2` 経由で入ります）。`DbConfig` の static 初期化ブロックでロードされるので、各DAOでは意識不要です。

### 3-2. 自分の PC で H2 サーバーを立てる場合

1. H2 の jar は Maven で落ちてきています。場所の例：
   ```
   ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar
   ```
2. TCP モードで起動します（外部からの接続も許可する `-tcpAllowOthers` 付き）。
   ```
   java -cp ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
     org.h2.tools.Server -tcp -tcpAllowOthers -ifNotExists
   ```
   - DB ファイルはユーザーホーム直下に作られます（`~/frenemy.mv.db`）。
   - 止めるときはターミナルで `Ctrl + C`。

### 3-3. スキーマと初期データの投入

リポジトリ直下の `db/schema.sql` が、テーブル定義と初期データを含むスクリプトです。  
H2 Console から流し込むのが一番簡単です。

1. H2 Console を起動：
   ```
   java -cp ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
     org.h2.tools.Server -web -webAllowOthers -browser
   ```
2. ブラウザが開くので、以下を入力して Connect。
   - JDBC URL : `jdbc:h2:tcp://localhost/~/frenemy`（他人のサーバーに繋ぐときは IP を変える）
   - ユーザー : `sa`
   - パスワード : 空
3. `db/schema.sql` の中身を貼り付けて `Run`。
   - 冒頭で `DROP TABLE IF EXISTS ...` しているので、何度流し直しても大丈夫です。

### 3-4. アプリから繋がらないとき

- `JDBCドライバを読み込めませんでした` → `pom.xml` に `h2` が入っているか、`Maven` → `Update Project` を実行。
- `Connection refused` → H2 サーバー側が起動していない / IP が違う / ファイアウォールでブロック。
- `Database "~/frenemy" not found` → H2 サーバーを `-ifNotExists` 付きで起動していない、またはサーバー側の `~/frenemy.mv.db` がまだ作られていない。先に `db/schema.sql` を流してください。

### 3-5. 家で開発する場合（環境変数で切り替え）

学校LAN外からは共用H2サーバー（`10.23.104.249`）に繋がらないので、
自PCのローカルH2を使います。手順は 2 ステップだけ。

**① 自PCのH2にスキーマを流す（初回のみ）**

1. H2 Console を起動する。
   ```
   java -cp ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
     org.h2.tools.Server -web -browser
   ```
2. ブラウザで以下を入力して Connect。
   - JDBC URL : `jdbc:h2:~/frenemy`
   - ユーザー : `sa`
   - パスワード : 空
3. `db/schema.sql` の中身を貼り付けて `Run`。

**② Eclipse の Run Configuration で環境変数を設定（必要なら）**

デフォルトで `jdbc:h2:~/frenemy;AUTO_SERVER=TRUE` を見るので、
**家で動かすだけなら環境変数は未設定でOK**。
学校の共用DBに切り替えるときだけ、下の手順で `DB_URL` を上書きします。

1. Eclipse のメニュー `Run` → `Run Configurations...`。
2. 左ペインから `Apache Tomcat` 以下の対象サーバーを選択。
3. 右ペインの `Environment` タブを開く → `Add...`。
4. 以下を追加：

   | Name | Value |
   |------|-------|
   | `DB_URL`  | `jdbc:h2:tcp://10.23.104.249/~/frenemy` （学校で共用DBに繋ぐ日だけ） |
   | `DB_USER` | `sa` （デフォルトでOK、省略可） |
   | `DB_PASS` | （空、省略可） |

5. `Apply` → `Run`。

> 家 ⇔ 学校 を切り替えるたびに `DB_URL` を書き換える／チェックボックスで有効無効を切り替える、という運用になります。コード側は触りません。

---

## 4. 共通ヘッダーの使い方

ヘッダーは共通部品です。他の JSP で使うときは次の include ディレクティブを書けば OK です。

```jsp
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
```
