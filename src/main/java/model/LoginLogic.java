package model;

import dao.AccountDAO;

/**
 * ログイン・新規登録の処理
 */
public class LoginLogic {
	/** 名前の最大文字数（accountテーブルの name 列に合わせる） */
	public static final int NAME_MAX = 50;
	/** パスワードの最小文字数 */
	public static final int PASS_MIN = 4;

	/**
	 * 名前とパスワードが合っていればそのユーザーを返す。合っていなければ null
	 */
	public User login(String name, String pass) {
		if (name == null || pass == null) {
			return null;
		}
		AccountDAO dao = new AccountDAO();
		Account account = dao.findByName(name);
		if (account == null || !PasswordUtil.verify(pass, account.getPassword())) {
			return null;
		}
		return new User(account.getUserId(), account.getName());
	}

	/**
	 * 新規登録。成功したら登録したユーザーを返す
	 * 入力ミスや名前の重複は IllegalArgumentException（メッセージをそのまま画面に出す）
	 */
	public User register(String name, String pass) {
		if (name == null || name.isBlank() || name.length() > NAME_MAX) {
			throw new IllegalArgumentException("ユーザー名は1〜" + NAME_MAX + "文字で入力してください。");
		}
		if (pass == null || pass.length() < PASS_MIN) {
			throw new IllegalArgumentException("パスワードは" + PASS_MIN + "文字以上で入力してください。");
		}
		AccountDAO dao = new AccountDAO();
		if (dao.findByName(name) != null) {
			throw new IllegalArgumentException("そのユーザー名は既に使われています。");
		}
		int userId = dao.insert(name, PasswordUtil.hash(pass));
		if (userId < 0) {
			throw new IllegalArgumentException("登録できませんでした。時間をおいてもう一度お試しください。");
		}
		return new User(userId, name);
	}
}
