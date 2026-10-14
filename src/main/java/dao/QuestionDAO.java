package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Choice;
import model.Question;

public class QuestionDAO {
	/** 1つの特性から出す問題数 */
	private static final int QUESTIONS_PER_PERSONALITY = 5;

	private static final String SELECT_COLUMNS =
			"SELECT question_id, question, critical, type, personality_id FROM question ";

	/**
	 * 出題リストを作る
	 * 各特性から5問ずつ ＋ 特殊質問（フェイク・フレネミー） ＋ 吉嵜質問1問 をシャッフル
	 * 失敗したら空のリストを返す
	 */
	public List<Question> findQuiz() {
		List<Question> quizList = new ArrayList<>();
		try (Connection conn = DbConfig.getConnection()) {

			//特性IDの一覧を取得
			List<Integer> personalityIds = new ArrayList<>();
			String sql = "SELECT personality_id FROM personality ORDER BY personality_id";
			try (PreparedStatement pStmt = conn.prepareStatement(sql);
					ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					personalityIds.add(rs.getInt("personality_id"));
				}
			}

			//各特性から通常質問をランダムに5問ずつ
			sql = SELECT_COLUMNS
					+ "WHERE type = 'NORMAL' AND personality_id = ? ORDER BY RAND() LIMIT ?";
			try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
				for (int personalityId : personalityIds) {
					pStmt.setInt(1, personalityId);
					pStmt.setInt(2, QUESTIONS_PER_PERSONALITY);
					addQuestions(pStmt, quizList);
				}
			}

			//特殊質問（フェイク・フレネミー）は全部入れる
			sql = SELECT_COLUMNS + "WHERE type <> 'NORMAL' AND critical = FALSE";
			try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
				addQuestions(pStmt, quizList);
			}

			//吉嵜質問を必ず1問混ぜる
			sql = SELECT_COLUMNS + "WHERE critical = TRUE ORDER BY RAND() LIMIT 1";
			try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
				addQuestions(pStmt, quizList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		Collections.shuffle(quizList);
		return quizList;
	}

	/**
	 * 回答の選択肢を取得（失敗したら空のリスト）
	 */
	public List<Choice> findChoices() {
		List<Choice> choices = new ArrayList<>();
		try (Connection conn = DbConfig.getConnection()) {

			String sql = "SELECT choice_id, label FROM answer_select ORDER BY choice_id";
			try (PreparedStatement pStmt = conn.prepareStatement(sql);
					ResultSet rs = pStmt.executeQuery()) {
				while (rs.next()) {
					choices.add(new Choice(rs.getInt("choice_id"), rs.getString("label")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		return choices;
	}

	private void addQuestions(PreparedStatement pStmt, List<Question> list) throws SQLException {
		try (ResultSet rs = pStmt.executeQuery()) {
			while (rs.next()) {
				list.add(new Question(
						rs.getInt("question_id"),
						rs.getString("question"),
						rs.getBoolean("critical"),
						rs.getString("type"),
						rs.getInt("personality_id")));
			}
		}
	}
}
