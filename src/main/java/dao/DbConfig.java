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
	private static final String DEFAULT_URL = "jdbc:h2:~/frenemy;AUTO_SERVER=TRUE";
	private static final String DEFAULT_USER = "sa";
	private static final String DEFAULT_PASS = "";

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
		String url = getEnvOrDefault("DB_URL", DEFAULT_URL);
		String user = getEnvOrDefault("DB_USER", DEFAULT_USER);
		String pass = getEnvOrDefault("DB_PASS", DEFAULT_PASS);
		return DriverManager.getConnection(url, user, pass);
	}

	private static String getEnvOrDefault(String name, String defaultValue) {
		String v = System.getenv(name);
		return v == null ? defaultValue : v;
	}
}
