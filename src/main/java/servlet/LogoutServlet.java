package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログアウト処理（POST のみ）。
 *
 * <p>GET を受けないのは CSRF 的な意味（画像タグなどからの意図しないログアウトを避ける）。
 * {@link HttpSession#invalidate()} で診断中データ・{@code loginUser} をまとめて破棄してから、
 * 開始画面に 302 リダイレクト。
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.sendRedirect(request.getContextPath() + "/StartServlet");
	}
}
