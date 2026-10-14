package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CounterDAO {
	public int Counter() {
		int count = 0;
		try (Connection conn = DbConfig.getConnection()) {

			//SELECT文を準備
			String sql = "SELECT COUNT(*) FROM result";
			PreparedStatement pStmt = conn.prepareStatement(sql);

			//SELECT文を実行し、結果表を取得
			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return count;
	}
}
