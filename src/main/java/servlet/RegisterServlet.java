package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.LoginLogic;
import model.User;

/**
 * 新規登録画面。
 *
 * <ul>
 *   <li>{@code GET} — 入力フォーム（{@code passMin} / {@code nameMax} を文言生成用に渡す）</li>
 *   <li>{@code POST} — {@link LoginLogic#register(String, String)} を呼び、
 *       成功したら {@link Login#loginAs(HttpServletRequest, User)} で即ログイン → {@code /StartServlet}。
 *       {@link IllegalArgumentException} はそのまま {@code errorMessage} として同じフォームへ戻す
 *       （入力ミス・名前重複など、ユーザーに見せたい文言は LoginLogic 側が持っている）</li>
 * </ul>
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("passMin", LoginLogic.PASS_MIN);
		request.setAttribute("nameMax", LoginLogic.NAME_MAX);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/register.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
		String pass = request.getParameter("pass");

		User user;
		try {
			user = new LoginLogic().register(name, pass);
		} catch (IllegalArgumentException e) {
			request.setAttribute("errorMessage", e.getMessage());
			request.setAttribute("name", name);
			doGet(request, response);
			return;
		}
		Login.loginAs(request, user);
		response.sendRedirect(request.getContextPath() + "/StartServlet");
	}
}
