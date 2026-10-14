package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CounterDAO {
	private final String JDBC_URL=
			"jdbc:h2:tcp://10.23.104.249/~/frenemy";
	private final String DB_USER="sa";
	private final String DB_PASS= "";
	
	public int Counter() {
		int count = 0;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) { 
			throw new IllegalStateException("JDBCドライバを読み込めませんでした");
		}
		try (Connection conn = DriverManager.getConnection(
				JDBC_URL,DB_USER,DB_PASS))	{
			
			//SELECT文を準備
			String sql="SELECT COUNT(*) FROM result";
			PreparedStatement pStmt = conn.prepareStatement(sql);
		
			//SELECT文を実行し、結果表を取得
			ResultSet rs = pStmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return count;
	}
}