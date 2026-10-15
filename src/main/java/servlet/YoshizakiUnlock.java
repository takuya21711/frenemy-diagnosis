package servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ResultDAO;
import model.Role;
import model.User;

/**
 * 役職一覧の「吉嵜」を解放したかどうか
 * ・このブラウザで吉嵜を出した → Cookie に記録
 * ・ログイン中のアカウントで吉嵜を出したことがある → 結果テーブルで判定
 */
final class YoshizakiUnlock {
	private static final String COOKIE_NAME = "yoshizaki";
	/** Cookie の有効期限（1年） */
	private static final int MAX_AGE = 60 * 60 * 24 * 365;

	private YoshizakiUnlock() {
	}

	/** 吉嵜を出したときに呼ぶ */
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
		HttpSession session = request.getSession(false);
		User loginUser = session == null ? null : (User) session.getAttribute(Login.LOGIN_USER);
		return loginUser != null && new ResultDAO().hasRole(loginUser.getUserId(), Role.YOSHIZAKI_ID);
	}
}
