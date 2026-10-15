package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * resultテーブルの行数（＝これまでに診断を受けた人数）を数える DAO
 * 開始画面と結果画面のカウンター表示に使う
 */
public class CounterDAO {
	/** これまでの診断回数。失敗時は -1 */
	public int count() {
		String sql = "SELECT COUNT(*) FROM result";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql);
				ResultSet rs = pStmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
