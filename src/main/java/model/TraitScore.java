package model;
import java.io.Serializable;

/**
 * 結果画面に表示する特性1つ分（特性名と正規化スコア）
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

	/** バー表示用：-30〜+30 を 0〜100 に変換 */
	public int getPercent() {
		return (score + 30) * 100 / 60;
	}
}
