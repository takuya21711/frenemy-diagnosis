package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Result;
import model.Role;
import model.Scores;
import model.TraitScore;

/**
 * resultテーブル（＋表示用に role / image / item / personality を結合）
 */
public class ResultDAO {
	/**
	 * 特性IDごとの result の列名（添字＝特性ID）
	 * 列は schema.sql で固定なので、ここだけ対応表を持つ
	 */
	private static final String[] SCORE_COLUMNS = {
			null, "extraversion", "agreeableness", "conscientiousness", "emotional_stability", "openness" };

	private static final String SELECT_RESULT =
			"SELECT r.result_id, r.created_at, r.extraversion, r.agreeableness, r.conscientiousness, "
			+ "r.emotional_stability, r.openness, "
			+ "ro.id, ro.name, ro.description, ro.is_black, i.path, it.name AS item_name "
			+ "FROM result r "
			+ "JOIN role ro ON r.role_id = ro.id "
			+ "LEFT JOIN image i ON r.image_id = i.image_id "
			+ "LEFT JOIN item it ON r.item_id = it.item_id ";

	/**
	 * 結果を保存して result_id を返す。失敗したら -1
	 * 画像とおすすめアイテムは、その役職のものからランダムに1つ選ぶ
	 * @param scores クリティカル吉嵜のときは null
	 * @param userId 未ログインなら null
	 */
	public int insert(int roleId, Scores scores, Integer userId) {
		try (Connection conn = DbConfig.getConnection()) {
			Integer imageId = pickRandomId(conn,
					"SELECT image_id FROM image WHERE role_id = ? ORDER BY RAND() LIMIT 1", roleId);
			Integer itemId = pickRandomId(conn,
					"SELECT item_id FROM item WHERE role_id = ? ORDER BY RAND() LIMIT 1", roleId);

			String sql = "INSERT INTO result (image_id, role_id, item_id, user_id, "
					+ "extraversion, agreeableness, conscientiousness, emotional_stability, openness) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				setNullableInt(pStmt, 1, imageId);
				pStmt.setInt(2, roleId);
				setNullableInt(pStmt, 3, itemId);
				setNullableInt(pStmt, 4, userId);
				for (int pid = 1; pid <= Scores.SIZE; pid++) {
					setNullableInt(pStmt, 4 + pid, scores == null ? null : scores.get(pid));
				}
				pStmt.executeUpdate();
				try (ResultSet rs = pStmt.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/** 1件取得（スコアつき）。無い・失敗したら null */
	public Result findById(int resultId) {
		try (Connection conn = DbConfig.getConnection()) {
			Result result = null;
			try (PreparedStatement pStmt = conn.prepareStatement(SELECT_RESULT + "WHERE r.result_id = ?")) {
				pStmt.setInt(1, resultId);
				try (ResultSet rs = pStmt.executeQuery()) {
					if (!rs.next()) {
						return null;
					}
					result = toResult(rs);
					// スコアがある（クリティカル以外）ときだけ特性名をつけて並べる
					if (rs.getObject(SCORE_COLUMNS[1]) != null) {
						addTraits(conn, rs, result);
					}
				}
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** ユーザーの診断履歴（新しい順）。失敗したら空のリスト */
	public List<Result> findByUser(int userId) {
		List<Result> list = new ArrayList<>();
		String sql = SELECT_RESULT + "WHERE r.user_id = ? ORDER BY r.created_at DESC, r.result_id DESC";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setInt(1, userId);
			try (ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					list.add(toResult(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		return list;
	}

	/** そのユーザーがその役職を出したことがあるか（吉嵜の解放判定に使う） */
	public boolean hasRole(int userId, int roleId) {
		String sql = "SELECT 1 FROM result WHERE user_id = ? AND role_id = ? LIMIT 1";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setInt(1, userId);
			pStmt.setInt(2, roleId);
			try (ResultSet rs = pStmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Result toResult(ResultSet rs) throws SQLException {
		Role role = new Role(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getBoolean("is_black"));
		return new Result(
				rs.getInt("result_id"),
				role,
				rs.getString("path"),
				rs.getString("item_name"),
				rs.getTimestamp("created_at"));
	}

	private void addTraits(Connection conn, ResultSet resultRs, Result result) throws SQLException {
		String sql = "SELECT personality_id, name FROM personality ORDER BY personality_id";
		try (PreparedStatement pStmt = conn.prepareStatement(sql);
				ResultSet rs = pStmt.executeQuery()) {
			while (rs.next()) {
				int pid = rs.getInt("personality_id");
				if (pid >= 1 && pid <= Scores.SIZE) {
					result.getTraits().add(new TraitScore(rs.getString("name"), resultRs.getInt(SCORE_COLUMNS[pid])));
				}
			}
		}
	}

	private Integer pickRandomId(Connection conn, String sql, int roleId) throws SQLException {
		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setInt(1, roleId);
			try (ResultSet rs = pStmt.executeQuery()) {
				return rs.next() ? rs.getInt(1) : null;
			}
		}
	}

	private void setNullableInt(PreparedStatement pStmt, int index, Integer value) throws SQLException {
		if (value == null) {
			pStmt.setNull(index, Types.INTEGER);
		} else {
			pStmt.setInt(index, value);
		}
	}
}
