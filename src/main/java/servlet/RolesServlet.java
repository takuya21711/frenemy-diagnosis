package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.RoleDAO;

/**
 * 役職一覧画面。
 *
 * <p>JSP に以下を渡す:
 * <ul>
 *   <li>{@code roles} — {@link RoleDAO#findAll()}（全役職、閾値 / 画像付き）</li>
 *   <li>{@code yoshizakiUnlocked} — 吉嵜解放済みかどうか（{@link YoshizakiUnlock#isUnlocked}）</li>
 * </ul>
 * 未解放の場合、JSP 側で吉嵜（{@value model.Role#YOSHIZAKI_ID}）は "？？？" で隠す。
 */
@WebServlet("/roles")
public class RolesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("roles", new RoleDAO().findAll());
		request.setAttribute("yoshizakiUnlocked", YoshizakiUnlock.isUnlocked(request));

		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/roles.jsp");
		dispatcher.forward(request, response);
	}
}
