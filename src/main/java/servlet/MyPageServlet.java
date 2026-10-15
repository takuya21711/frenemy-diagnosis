package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ResultDAO;
import model.User;

/**
 * マイページ（ログイン必須）。ユーザー情報と診断履歴を表示
 */
@WebServlet("/mypage")
public class MyPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User loginUser = LoginSession.currentUser(request);
		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/Login");
			return;
		}
		request.setAttribute("history", new ResultDAO().findByUser(loginUser.getUserId()));

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/mypage.jsp");
		dispatcher.forward(request, response);
	}
}
