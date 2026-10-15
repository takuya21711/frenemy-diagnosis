package model;
import java.io.Serializable;

/**
 * 5特性の正規化スコア（{@value model.ScoreCalculator#NORM_MIN} 〜 {@value model.ScoreCalculator#NORM_MAX}）。
 *
 * <p>特性IDは {@code personality} テーブルと同じで、1〜{@value #SIZE}:
 * <ol>
 *   <li>外向性 (extraversion)</li>
 *   <li>協調性 (agreeableness)</li>
 *   <li>誠実性 (conscientiousness)</li>
 *   <li>情緒安定性 (emotional stability)</li>
 *   <li>開放性 (openness)</li>
 * </ol>
 *
 * <p>内部配列は添字 0 を使わず、特性IDをそのまま添字にする。
 */
public class Scores implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 特性の数 */
	public static final int SIZE = 5;

	/** 添字 0 は使わず、特性IDをそのまま添字にする */
	private final int[] values = new int[SIZE + 1];

	public Scores() {}

	/** @param personalityId 1〜{@value #SIZE} */
	public int get(int personalityId) {
		return values[personalityId];
	}

	/** @param personalityId 1〜{@value #SIZE} */
	public void set(int personalityId, int score) {
		values[personalityId] = score;
	}

	public int getExtraversion() {return values[1];}
	public int getAgreeableness() {return values[2];}
	public int getConscientiousness() {return values[3];}
	public int getEmotionalStability() {return values[4];}
	public int getOpenness() {return values[5];}
}
