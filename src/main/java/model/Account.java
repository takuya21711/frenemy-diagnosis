package model;

/**
 * {@code account} テーブルの1行（ログイン判定にだけ使う）。
 *
 * <p>セッションにはこのクラスではなく {@link User}（ハッシュを含まない表現）を入れる。
 * ハッシュ形式は {@link PasswordUtil} の "salt:hash" (Base64) 固定。
 */
public class Account {
	private int userId;
	private String name;
	/** "salt:hash" 形式のハッシュ */
	private String password;

	public Account(int userId, String name, String password) {
		this.userId = userId;
		this.name = name;
		this.password = password;
	}
	public int getUserId() {return userId;}
	public String getName() {return name;}
	public String getPassword() {return password;}
}
