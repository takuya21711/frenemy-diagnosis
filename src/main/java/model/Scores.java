package model;
import java.io.Serializable;

/**
 * 5特性の正規化スコア（-30〜+30）
 * 特性IDは personality テーブルと同じ（1=外向性 2=協調性 3=誠実性 4=情緒安定性 5=開放性）
 */
public class Scores implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 特性の数 */
	public static final int SIZE = 5;

	/** 添字 0 は使わず、特性IDをそのまま添字にする */
	private final int[] values = new int[SIZE + 1];

	public Scores() {}

	public int get(int personalityId) {
		return values[personalityId];
	}
	public void set(int personalityId, int score) {
		values[personalityId] = score;
	}

	public int getExtraversion() {return values[1];}
	public int getAgreeableness() {return values[2];}
	public int getConscientiousness() {return values[3];}
	public int getEmotionalStability() {return values[4];}
	public int getOpenness() {return values[5];}
}
