package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Result;

/**
 * クリティカル吉嵜の演出画面（{@code /critical?id=<resultId>}）。
 *
 * <p>この画面に来た時点で結果は {@link QuizServlet} が既に保存済み。ここは読むだけ。
 * {@code ?id} が吉嵜（{@value model.Role#YOSHIZAKI_ID}）以外の結果 /
 * 不正な ID を指している場合は不正操作とみなして {@link StartServlet} へ戻す。
 */
@WebServlet("/critical")
public class CriticalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Result result = ResultServlet.findResult(request);
		//吉嵜の結果でなければ（URLを直接打った等）開始画面へ
		if (result == null || !result.getRole().isYoshizaki()) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		request.setAttribute("result", result);

		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/critical.jsp");
		dispatcher.forward(request, response);
	}
}
