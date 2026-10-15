package model;

/**
 * accountテーブルの1行（ログイン判定にだけ使う。セッションには User を入れる）
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
