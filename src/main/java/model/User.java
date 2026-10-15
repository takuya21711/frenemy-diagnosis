package model;
import java.io.Serializable;

/**
 * ログイン中のユーザー（セッションの loginUser）
 * パスワードはセッションに持たせない
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
