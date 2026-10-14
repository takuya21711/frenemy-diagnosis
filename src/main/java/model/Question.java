package model;
import java.io.Serializable;

/**
 * 質問（questionテーブルの1行）
 */
public class Question implements Serializable {
	private static final long serialVersionUID = 1L;

	private int questionId;
	private String question;
	/** TRUEなら「とても当てはまる」でクリティカル吉嵜発動 */
	private boolean critical;
	/** NORMAL / FAKE / FRENEMY */
	private String type;
	/** 特性ID（特殊質問は0） */
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
