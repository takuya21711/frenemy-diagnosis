package model;
import java.io.Serializable;

/**
 * ログイン中のユーザー（{@code HttpSession} の {@code loginUser} 属性に乗る）。
 *
 * <p>セキュリティのため、ハッシュ化後のパスワードもセッションには持たせない。
 * 認証判定が必要なときは {@link Account} をその都度 DB から読む。
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private int userId;
	private String name;

	public User() {}
	public User(int userId, String name) {
		this.userId = userId;
		this.name = name;
	}
	public int getUserId() {return userId;}
	public String getName() {return name;}
}
