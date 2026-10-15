package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Answer;

/**
 * {@code scoring} / {@code fixed_score} 参照用の DAO（スコア計算の入力を揃える役割）。
 *
 * <p>画面から送られてくる {@link Answer} の点数そのものは信用せず、
 * (question_id, choice_id) で改めて DB を引く設計。
 * 結果は {@link model.ScoreCalculator#calculate} の引数にそのまま渡せる形。
 *
 * <p>SQL の値はすべて {@code PreparedStatement} プレースホルダ経由（IN 句も含む）。
 */
public class ScoringDAO {

	/**
	 * 回答した質問の点数表を取得する
	 * @return 質問ID → (選択肢ID → 点数)。失敗したら null
	 */
	public Map<Integer, Map<Integer, Integer>> findScoring(List<Answer> answers) {
		Map<Integer, Map<Integer, Integer>> scoring = new HashMap<>();
		if (answers.isEmpty()) {
			return scoring;
		}
		String sql = "SELECT question_id, choice_id, score FROM scoring WHERE question_id IN ("
				+ placeholders(answers.size()) + ")";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			setQuestionIds(pStmt, answers);
			try (ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					scoring.computeIfAbsent(rs.getInt("question_id"), k -> new HashMap<>())
							.put(rs.getInt("choice_id"), rs.getInt("score"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return scoring;
	}

	/**
	 * 回答の中に「固定スコア」になる組み合わせ（フレネミー質問で「とても当てはまる」など）があれば、その値を返す
	 * @return 特性ID → 固定スコア（当たっていなければ空）。失敗したら null
	 */
	public Map<Integer, Integer> findFixedScores(List<Answer> answers) {
		Map<Integer, Integer> fixedScores = new HashMap<>();
		if (answers.isEmpty()) {
			return fixedScores;
		}
		String sql = "SELECT question_id, choice_id, personality_id, score FROM fixed_score WHERE question_id IN ("
				+ placeholders(answers.size()) + ")";
		try (Connection conn = DbConfig.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			setQuestionIds(pStmt, answers);
			try (ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					if (isAnswered(answers, rs.getInt("question_id"), rs.getInt("choice_id"))) {
						fixedScores.put(rs.getInt("personality_id"), rs.getInt("score"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return fixedScores;
	}

	private boolean isAnswered(List<Answer> answers, int questionId, int choiceId) {
		for (Answer a : answers) {
			if (a.getQuestionId() == questionId && a.getChoiceId() == choiceId) {
				return true;
			}
		}
		return false;
	}

	/** "?, ?, ?" を作る（IN句の値も必ずプレースホルダで渡す） */
	private String placeholders(int n) {
		return String.join(", ", Collections.nCopies(n, "?"));
	}

	private void setQuestionIds(PreparedStatement pStmt, List<Answer> answers) throws SQLException {
		for (int i = 0; i < answers.size(); i++) {
			pStmt.setInt(i + 1, answers.get(i).getQuestionId());
		}
	}
}
