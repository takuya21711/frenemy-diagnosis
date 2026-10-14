package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * クリティカル吉嵜画面（爆発 → 吉嵜を表示）
 * 診断中に吉嵜質問で「とても当てはまる」を選んだ人だけ表示できる
 */
@WebServlet("/critical")
public class CriticalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || !Boolean.TRUE.equals(session.getAttribute(QuizServlet.CRITICAL))) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		// TODO: 結果テーブルに吉嵜(role_id=15)として保存し、役職一覧で吉嵜を解放する

		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/critical.jsp");
		dispatcher.forward(request, response);
	}
}
