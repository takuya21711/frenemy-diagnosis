package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * スコア計算（DBには触らない。必要なデータは引数でもらう）。
 *
 * <ol>
 *   <li>通常質問 ({@code NORMAL}) の点数を特性ごとに合計（素点）。
 *       フェイク / フレネミー / 吉嵜質問は計算に入れない</li>
 *   <li>min-max 正規化で {@value #NORM_MIN} 〜 {@value #NORM_MAX} に揃える。
 *       素点の最小・最大は「出題された質問それぞれの選択肢の最低点・最高点」の合計なので、
 *       質問数や点数を DB で変えてもそのまま動く</li>
 *   <li>フレネミー質問の固定スコアに当たっていれば、その特性を固定値で上書きする</li>
 * </ol>
 *
 * <p>このクラスは DB アクセスをしない（単体テストしやすいようにデータは呼び出し側で用意する）。
 * 呼び出し元: {@link servlet.QuizServlet#saveResult}。
 */
public class ScoreCalculator {
	/** 正規化後の範囲 */
	public static final int NORM_MIN = -30;
	public static final int NORM_MAX = 30;

	/**
	 * @param questions   出題リスト
	 * @param answers     回答リスト
	 * @param scoring     質問ID → (選択肢ID → 点数)
	 * @param fixedScores 特性ID → 固定スコア（フレネミー質問に当たっていなければ空）
	 */
	public Scores calculate(List<Question> questions, List<Answer> answers,
			Map<Integer, Map<Integer, Integer>> scoring, Map<Integer, Integer> fixedScores) {
		Map<Integer, Question> questionById = new HashMap<>();
		for (Question q : questions) {
			questionById.put(q.getQuestionId(), q);
		}

		int[] raw = new int[Scores.SIZE + 1];
		int[] min = new int[Scores.SIZE + 1];
		int[] max = new int[Scores.SIZE + 1];
		for (Answer answer : answers) {
			Question q = questionById.get(answer.getQuestionId());
			Map<Integer, Integer> choiceScores = scoring.get(answer.getQuestionId());
			if (q == null || choiceScores == null || !isScored(q)) {
				continue;
			}
			int pid = q.getPersonalityId();
			raw[pid] += choiceScores.getOrDefault(answer.getChoiceId(), 0);
			min[pid] += Collections.min(choiceScores.values());
			max[pid] += Collections.max(choiceScores.values());
		}

		Scores scores = new Scores();
		for (int pid = 1; pid <= Scores.SIZE; pid++) {
			scores.set(pid, normalize(raw[pid], min[pid], max[pid]));
		}
		for (Map.Entry<Integer, Integer> e : fixedScores.entrySet()) {
			scores.set(e.getKey(), e.getValue());
		}
		return scores;
	}

	/** 採点する質問か（通常質問で、特性が決まっているもの） */
	private boolean isScored(Question q) {
		return "NORMAL".equals(q.getType())
				&& q.getPersonalityId() >= 1 && q.getPersonalityId() <= Scores.SIZE;
	}

	/** min-max正規化： (x - min) / (max - min) を NORM_MIN〜NORM_MAX に広げる */
	static int normalize(int raw, int min, int max) {
		if (max == min) {
			// その特性の質問が1問も無いときは真ん中（0）
			return (NORM_MIN + NORM_MAX) / 2;
		}
		double ratio = (double) (raw - min) / (max - min);
		return (int) Math.round(ratio * (NORM_MAX - NORM_MIN) + NORM_MIN);
	}
}
