package model;
import java.io.Serializable;

/**
 * 役職の閾値（role_thresholdテーブルの1行）
 * 正規化スコアが min〜max に入っていれば一致
 */
public class Threshold implements Serializable {
	private static final long serialVersionUID = 1L;

	private int personalityId;
	private int min;
	private int max;

	public Threshold() {}
	public Threshold(int personalityId, int min, int max) {
		this.personalityId = personalityId;
		this.min = min;
		this.max = max;
	}
	public int getPersonalityId() {return personalityId;}
	public int getMin() {return min;}
	public int getMax() {return max;}

	public boolean contains(int score) {
		return min <= score && score <= max;
	}
}
