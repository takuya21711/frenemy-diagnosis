package model;
import java.io.Serializable;

/**
 * 回答の選択肢（{@code answer_select} テーブルの1行）。
 *
 * <p>初期データでは 1〜4 の 4 段階（1=「とても当てはまる」… 4=「当てはまらない」）。
 * {@code choice_id = 1} はクリティカル吉嵜のトリガー条件にも使われる
 * （{@code servlet.QuizServlet.CRITICAL_CHOICE_ID} 参照）。
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
