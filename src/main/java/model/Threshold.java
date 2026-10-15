package model;
import java.io.Serializable;

/**
 * 役職の閾値（{@code role_threshold} テーブルの1行）。
 *
 * <p>ある特性の正規化スコアが {@link #getMin()} 〜 {@link #getMax()} の範囲
 * （両端含む）に入っていれば「一致」とみなす。
 * 一致数が最大の役職を選ぶのは {@link RoleJudge}。
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
