package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.LoginLogic;
import model.User;

/**
 * ログイン画面
 * GET  : 入力フォーム
 * POST : 名前とパスワードを確認。成功したら開始画面へ、失敗したらフォームにエラー表示
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//リクエストパラメーターの取得
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
		String pass = request.getParameter("pass");

		//ログイン処理
		User user = new LoginLogic().login(name, pass);
		if (user == null) {
			request.setAttribute("errorMessage", "ユーザー名またはパスワードが違います。");
			request.setAttribute("name", name);
			doGet(request, response);
			return;
		}
		loginAs(request, user);
		response.sendRedirect(request.getContextPath() + "/StartServlet");
	}

	/** ログイン状態にする（新規登録のあとにも使う） */
	static void loginAs(HttpServletRequest request, User user) {
		HttpSession session = request.getSession();
		//セッション固定攻撃の対策：ログインしたらセッションIDを変える
		request.changeSessionId();
		session.setAttribute(SessionKeys.LOGIN_USER, user);
	}
}
