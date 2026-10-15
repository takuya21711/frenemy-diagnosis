package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Account;

/**
 * accountテーブル
 */
public class AccountDAO {

	/** 名前で探す。見つからない・失敗したら null */
	public Account findByName(String name) {
		String sql = "SELECT user_id, name, password FROM account WHERE name = ?";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setString(1, name);
			try (ResultSet rs = pStmt.executeQuery()) {
				if (rs.next()) {
					return new Account(rs.getInt("user_id"), rs.getString("name"), rs.getString("password"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 登録して user_id を返す。失敗したら（名前の重複も含む）-1
	 * @param passwordHash PasswordUtil.hash() した値
	 */
	public int insert(String name, String passwordHash) {
		String sql = "INSERT INTO account (name, password) VALUES (?, ?)";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pStmt.setString(1, name);
			pStmt.setString(2, passwordHash);
			pStmt.executeUpdate();
			try (ResultSet rs = pStmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
