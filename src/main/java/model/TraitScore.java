package model;
import java.io.Serializable;

/**
 * 結果画面に表示する特性1つ分。
 *
 * <p>{@link Scores} が特性ID → スコアの配列なのに対して、こちらは JSP に渡しやすいよう
 * 特性名を併せ持った表示用 DTO。
 */
public class TraitScore implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private int score;

	public TraitScore() {}
	public TraitScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	public String getName() {return name;}
	public int getScore() {return score;}

	/**
	 * バー表示用のパーセンテージ。
	 * 正規化スコア {@value model.ScoreCalculator#NORM_MIN} 〜 {@value model.ScoreCalculator#NORM_MAX} を
	 * 0 〜 100 に線形変換して返す。
	 */
	public int getPercent() {
		return (score + 30) * 100 / 60;
	}
}
