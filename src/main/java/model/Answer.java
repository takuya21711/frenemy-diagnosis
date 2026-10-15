package model;
import java.io.Serializable;

/**
 * 1問分の回答（{@code HttpSession} の {@code answerList} に蓄積される）。
 *
 * <p>点数そのものは持たない。採点時に {@link dao.ScoringDAO#findScoring} が
 * {@code scoring} テーブルから (question_id, choice_id) → score を引く。
 * 画面から送られてくる値をそのまま信頼せず、サーバー側で質問×選択肢の組を照合する前提の設計。
 */
public class Answer implements Serializable {
	private static final long serialVersionUID = 1L;

	private int questionId;
	private int choiceId;

	public Answer() {}
	public Answer(int questionId, int choiceId) {
		this.questionId = questionId;
		this.choiceId = choiceId;
	}
	public int getQuestionId() {return questionId;}
	public int getChoiceId() {return choiceId;}
}
