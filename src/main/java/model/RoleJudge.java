package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 役職判定
 * 5特性のうち、役職の閾値に入っている数（一致数）が一番多い役職を選ぶ
 * 同率1位が複数あればランダム。閾値の無い役職（吉嵜）は対象外
 */
public class RoleJudge {
	private final Random random;

	public RoleJudge() {
		this(new Random());
	}
	public RoleJudge(Random random) {
		this.random = random;
	}

	/** roles が空なら null */
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
