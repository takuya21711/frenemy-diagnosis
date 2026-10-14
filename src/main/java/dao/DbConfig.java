package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
