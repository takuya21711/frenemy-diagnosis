package model;
import java.io.Serializable;

/**
 * 1問分の回答（セッションの回答リストに入れる）
 * 点数は持たない。スコア計算時にDBの scoring テーブルから引く
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
