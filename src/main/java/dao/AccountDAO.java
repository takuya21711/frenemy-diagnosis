package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Account;

/**
 * {@code account} テーブルの CRUD（今は read / insert のみ）。
 * ログイン判定と新規登録に使う。
 */
public class AccountDAO {

	/**
	 * ユーザー名でアカウントを 1 件取得する。
	 * @return 見つかれば {@link Account}（ハッシュ化されたパスワードを含む）。無い / SQL 失敗なら {@code null}
	 */
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
	 * 新規アカウントを 1 件登録する。
	 * @param passwordHash {@link model.PasswordUtil#hash(String)} の返り値（"salt:hash", Base64）
	 * @return 発番された {@code user_id}。SQL 失敗（{@code name} が UNIQUE 制約違反のケース含む）なら {@code -1}
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
