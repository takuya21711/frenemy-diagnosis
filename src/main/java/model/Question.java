package model;
import java.io.Serializable;

/**
 * 質問（{@code question} テーブルの1行）。
 *
 * <p>出題時に {@code HttpSession} の {@code quizList} に乗る → {@code Serializable}。
 * 画面には ID と本文だけ出し、点数計算は {@link ScoreCalculator} が
 * ID から {@code scoring} テーブルを引く。
 */
public class Question implements Serializable {
	private static final long serialVersionUID = 1L;

	private int questionId;
	private String question;
	/** {@code TRUE} なら「とても当てはまる」(choice_id=1) でクリティカル吉嵜発動 */
	private boolean critical;
	/** {@code NORMAL} / {@code FAKE} / {@code FRENEMY}（{@link ScoreCalculator} で判定） */
	private String type;
	/** 特性ID（1〜{@value Scores#SIZE}）。特殊質問は {@code 0}（personality_id が NULL） */
	private int personalityId;

	public Question() {}
	public Question(int questionId, String question, boolean critical, String type, int personalityId) {
		this.questionId = questionId;
		this.question = question;
		this.critical = critical;
		this.type = type;
		this.personalityId = personalityId;
	}
	public int getQuestionId() {return questionId;}
	public String getQuestion() {return question;}
	public boolean isCritical() {return critical;}
	public String getType() {return type;}
	public int getPersonalityId() {return personalityId;}
}
