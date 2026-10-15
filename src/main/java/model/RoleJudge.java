package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 役職判定（通常診断の最後に呼ばれる）。
 *
 * <p>5特性のうち、役職の閾値に入っている数（一致数）が一番多い役職を選ぶ。
 * 同率1位が複数あればランダム。閾値の無い役職（吉嵜：{@value model.Role#YOSHIZAKI_ID}）は対象外
 * （吉嵜はクリティカル経路でだけ採用される）。
 *
 * <p>テスト容易性のため {@link Random} を外から渡せるコンストラクタを用意している。
 */
public class RoleJudge {
	private final Random random;

	public RoleJudge() {
		this(new Random());
	}
	public RoleJudge(Random random) {
		this.random = random;
	}

	/**
	 * @return 一致数が最大の役職。候補が無い（全役職が閾値を持たない or {@code roles} が空）場合は {@code null}
	 */
	public Role judge(List<Role> roles, Scores scores) {
		List<Role> best = new ArrayList<>();
		int bestCount = -1;
		for (Role role : roles) {
			if (role.getThresholds().isEmpty()) {
				continue;
			}
			int count = countMatches(role, scores);
			if (count > bestCount) {
				best.clear();
				bestCount = count;
			}
			if (count == bestCount) {
				best.add(role);
			}
		}
		if (best.isEmpty()) {
			return null;
		}
		return best.get(random.nextInt(best.size()));
	}

	/** 閾値に入っている特性の数 */
	public int countMatches(Role role, Scores scores) {
		int count = 0;
		for (Threshold t : role.getThresholds()) {
			if (t.contains(scores.get(t.getPersonalityId()))) {
				count++;
			}
		}
		return count;
	}
}
