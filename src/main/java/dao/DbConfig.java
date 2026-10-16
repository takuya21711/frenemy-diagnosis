package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 全 DAO が共通で使う JDBC 接続設定。
 *
 * <p>接続情報は環境変数で上書き可能:
 * <ul>
 *   <li>{@code DB_URL}  — 未設定なら {@value #DEFAULT_URL}</li>
 *   <li>{@code DB_USER} — 未設定なら {@value #DEFAULT_USER}</li>
 *   <li>{@code DB_PASS} — 未設定なら空文字列</li>
 * </ul>
 *
 * <p>H2 JDBC ドライバのロードは static 初期化で 1 回だけ行う（各 DAO からは意識不要）。
 */
public final class DbConfig {
	// 共用H2サーバー（TCPモード）に固定。IPが変わったらここを書き換える。
	private static final String URL = "jdbc:h2:tcp://10.23.104.249/~/frenemy";
	private static final String USER = "sa";
	private static final String PASS = "";

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("JDBCドライバを読み込めませんでした", e);
		}
	}

	private DbConfig() {
	}

	/**
	 * 新しい JDBC コネクションを返す。呼び出し側は {@code try-with-resources} で閉じること。
	 */
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
}
