package servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ResultDAO;
import model.Role;
import model.User;

/**
 * 役職一覧画面での「吉嵜」解放判定。
 *
 * <p>解放されるのは次のいずれか:
 * <ul>
 *   <li>このブラウザで一度でも吉嵜を出した（Cookie {@value #COOKIE_NAME}=1 が残っている。有効期限 1 年）</li>
 *   <li>ログイン中のアカウントの過去結果に吉嵜（{@value model.Role#YOSHIZAKI_ID}）がある
 *       （{@link ResultDAO#hasRole}）</li>
 * </ul>
 */
final class YoshizakiUnlock {
	private static final String COOKIE_NAME = "yoshizaki";
	/** Cookie の有効期限（1年） */
	private static final int MAX_AGE = 60 * 60 * 24 * 365;

	private YoshizakiUnlock() {
	}

	/**
	 * 吉嵜を出したときに呼ぶ：Cookie を 1 年間付ける。
	 * {@code HttpOnly} 付きで JS からは触れない。
	 */
	static void unlock(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, "1");
		cookie.setMaxAge(MAX_AGE);
		String path = request.getContextPath();
		cookie.setPath(path.isEmpty() ? "/" : path);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	static boolean isUnlocked(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (COOKIE_NAME.equals(c.getName()) && "1".equals(c.getValue())) {
					return true;
				}
			}
		}
		User loginUser = LoginSession.currentUser(request);
		return loginUser != null && new ResultDAO().hasRole(loginUser.getUserId(), Role.YOSHIZAKI_ID);
	}
}
