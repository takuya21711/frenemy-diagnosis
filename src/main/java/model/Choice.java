package model;
import java.io.Serializable;

/**
 * 回答の選択肢（answer_selectテーブルの1行）
 */
public class Choice implements Serializable {
	private static final long serialVersionUID = 1L;

	private int choiceId;
	private String label;

	public Choice() {}
	public Choice(int choiceId, String label) {
		this.choiceId = choiceId;
		this.label = label;
	}
	public int getChoiceId() {return choiceId;}
	public String getLabel() {return label;}
}
