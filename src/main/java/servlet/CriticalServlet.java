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
 * クリティカル吉嵜画面（爆発 → 吉嵜を表示 → 結果画面へ）
 * 結果は QuizServlet が保存済み。ここでは ?id= の結果を読むだけ
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
