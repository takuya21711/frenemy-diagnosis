package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Answer;

/**
 * 結果画面（仮）
 * TODO: スコア計算 → 役職判定 → 結果テーブルに保存 → result?id=結果ID にリダイレクト
 */
@WebServlet("/result")
public class ResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		List<Answer> answerList = session == null ? null
				: (List<Answer>) session.getAttribute(QuizServlet.ANSWER_LIST);
		if (answerList == null) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		request.setAttribute("answerCount", answerList.size());

		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
		dispatcher.forward(request, response);
	}
}
