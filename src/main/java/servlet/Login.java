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
 * ログイン画面。
 *
 * <ul>
 *   <li>{@code GET} — 入力フォーム（{@code login.jsp}）</li>
 *   <li>{@code POST} — {@link LoginLogic#login(String, String)} で判定。
 *       成功したら {@link #loginAs(HttpServletRequest, User)} で状態を切り替え、
 *       {@code /StartServlet} へ 302。失敗したら {@code errorMessage} 付きで同じフォームに戻す。</li>
 * </ul>
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

	/**
	 * セッションをログイン状態に遷移させる（新規登録成功後も呼ぶ）。
	 *
	 * <p>セッション固定攻撃を避けるため、{@link HttpServletRequest#changeSessionId()}
	 * でセッションIDを付け替えてから {@code loginUser} を書き込む。
	 */
	static void loginAs(HttpServletRequest request, User user) {
		HttpSession session = request.getSession();
		//セッション固定攻撃の対策：ログインしたらセッションIDを変える
		request.changeSessionId();
		session.setAttribute(SessionKeys.LOGIN_USER, user);
	}
}
