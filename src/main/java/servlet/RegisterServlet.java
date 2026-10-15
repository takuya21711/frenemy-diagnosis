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
 * 新規登録画面
 * GET  : 入力フォーム
 * POST : 登録してそのままログイン → 開始画面へ
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
