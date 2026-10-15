package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Role;
import model.Threshold;

/**
 * roleテーブル・role_thresholdテーブル参照
 */
public class RoleDAO {

	/**
	 * 全役職を閾値つきで取得（ID順）
	 * 失敗したら空のリスト
	 */
	public List<Role> findAll() {
		Map<Integer, Role> roles = new LinkedHashMap<>();
		try (Connection conn = DbConfig.getConnection()) {

			//役職一覧で見せる画像は、その役職の画像のうち最初の1枚
			String sql = "SELECT r.id, r.name, r.description, r.is_black, "
					+ "(SELECT MIN(i.path) FROM image i WHERE i.role_id = r.id) AS image_path "
					+ "FROM role r ORDER BY r.id";
			try (PreparedStatement pStmt = conn.prepareStatement(sql);
					ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					Role role = new Role(
							rs.getInt("id"),
							rs.getString("name"),
							rs.getString("description"),
							rs.getBoolean("is_black"));
					role.setImagePath(rs.getString("image_path"));
					roles.put(role.getId(), role);
				}
			}

			sql = "SELECT role_id, personality_id, min_score, max_score FROM role_threshold "
					+ "ORDER BY role_id, personality_id";
			try (PreparedStatement pStmt = conn.prepareStatement(sql);
					ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					Role role = roles.get(rs.getInt("role_id"));
					if (role != null) {
						role.getThresholds().add(new Threshold(
								rs.getInt("personality_id"),
								rs.getInt("min_score"),
								rs.getInt("max_score")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		return new ArrayList<>(roles.values());
	}
}
