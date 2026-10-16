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
| H2 Database | 2.3.x | DAO は共用 H2 サーバー（TCP モード）に接続するので、サーバー側で H2 が起動している必要があります |

> Jakarta EE 系（`jakarta.servlet.*`）を使っているので、**Tomcat 9 ではビルドは通っても動きません**。必ず Tomcat 10 以降（できれば 11）を入れてください。

---

## 2. 環境構築

1. 任意のフォルダに clone します。
   ```
   git clone <このリポジトリのURL> frenemy-diagnosis
   ```
2. Eclipse を起動 →  
   `ファイル` → `インポート...` → `Maven` → `既存Mavenプロジェクト` を選択。
3. `ルート・ディレクトリー` に clone したフォルダを指定して `完了`。
   - m2e が `pom.xml` を読んで、依存ライブラリを自動で取得します。
4. プロジェクトを右クリック → `Maven` → `プロジェクトの更新...`（`Alt + F5`）を一度実行しておくと確実です。
5. Tomcat 11 を Eclipse に登録します。
   - `ウィンドウ` → `設定` → `サーバー` → `ランタイム環境` → `追加...`
   - `Apache Tomcat v11.0` を選び、インストール済み Tomcat のフォルダを指定。
6. プロジェクトを右クリック → `実行` → `サーバーで実行` → 先ほど登録した Tomcat を選択。
7. ブラウザで次の URL を開いて動作確認してください。
   ```
   http://localhost:8080/frenemy-diagnosis/
   ```
---

## 2. DB（H2）の接続方法

このアプリは、**共用の H2 サーバー 1 つだけ** に接続します。環境変数での切り替えや、ローカルファイルモードへのフォールバックはありません。

### 2-1. 接続情報

接続情報は `src/main/java/dao/DbConfig.java` に固定で書かれています。

| 項目 | 値 |
|------|----|
| JDBC URL   | `jdbc:h2:tcp://10.23.104.249/~/frenemy` |
| ユーザー   | `sa` |
| パスワード | （空） |

> `10.23.104.249` は **DBを立てている人のPCのローカルIP** です。学校LAN内からしか繋がりません。
> IP が変わったときは `DbConfig.java` の `URL` を書き換えてください。

ドライバは `org.h2.Driver`（`pom.xml` の `com.h2database:h2` 経由で入ります）。`DbConfig` の static 初期化ブロックでロードされるので、各DAOでは意識不要です。

### 2-2. H2 サーバーを立てる場合（DB 担当者向け）

共用 DB を立てる人（`DbConfig.java` の IP の PC）だけが行う作業です。

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

### 2-3. スキーマと初期データの投入

リポジトリ直下の `db/schema.sql` が、テーブル定義と初期データを含むスクリプトです。  
H2 Console から流し込むのが一番簡単です。

1. H2 Console を起動：
   ```
   java -cp ~/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar \
     org.h2.tools.Server -web -webAllowOthers -browser
   ```
2. ブラウザが開くので、以下を入力して Connect。
   - JDBC URL : `jdbc:h2:tcp://10.23.104.249/~/frenemy`（サーバーを立てている PC 上で開くなら `localhost` でも可）
   - ユーザー : `sa`
   - パスワード : 空
3. `db/schema.sql` の中身を貼り付けて `Run`。
   - 冒頭で `DROP TABLE IF EXISTS ...` しているので、何度流し直しても大丈夫です。

### 2-4. アプリから繋がらないとき

- `JDBCドライバを読み込めませんでした` → `pom.xml` に `h2` が入っているか、`Maven` → `プロジェクトの更新` を実行。
- `Connection refused` / `タイムアウト` → H2 サーバー側が起動していない / IP が違う（`DbConfig.java` の `URL` を確認） / ファイアウォールでブロック / 学校LANに繋がっていない。
- `Database "~/frenemy" not found` → H2 サーバーを `-ifNotExists` 付きで起動していない、またはサーバー側の `~/frenemy.mv.db` がまだ作られていない。先に `db/schema.sql` を流してください。

---

## 4. 共通ヘッダーの使い方

ヘッダーは共通部品です。他の JSP で使うときは次の include ディレクティブを書けば OK です。

```jsp
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
```
