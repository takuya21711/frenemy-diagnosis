package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ルート {@code /}（例: {@code http://localhost:8080/frenemy-diagnosis/}）のサーブレット。
 *
 * <p>この診断アプリの「トップ」はそのまま診断開始画面なので、ここでは何も表示せず
 * {@link StartServlet} へ 302 リダイレクトする。
 */
@WebServlet("")
public class TopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/StartServlet");
	}
}
