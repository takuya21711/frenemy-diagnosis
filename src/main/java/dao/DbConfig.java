package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
}
