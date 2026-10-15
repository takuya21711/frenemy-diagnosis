package servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import model.User;

/**
 * セッション上のログイン状態をまとめて扱うヘルパー。
 * 「{@code (User) session.getAttribute(LOGIN_USER)}」というキャスト付きの定型を1箇所に閉じる。
 */
final class LoginSession {

	private LoginSession() {
	}

	/**
	 * ログイン中のユーザー。未ログイン（セッション無し含む）なら {@code null}
	 */
	static User currentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session == null ? null : (User) session.getAttribute(SessionKeys.LOGIN_USER);
	}

	/**
	 * ログイン中のユーザーID。未ログインなら {@code null}
	 * （DAO に渡す {@code Integer userId} 用）
	 */
	static Integer currentUserId(HttpServletRequest request) {
		User user = currentUser(request);
		return user == null ? null : user.getUserId();
	}
}
