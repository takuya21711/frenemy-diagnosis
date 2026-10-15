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
 * 役職一覧画面
 * 吉嵜は、自分で出すまで「？？？」にしておく（YoshizakiUnlock）
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
