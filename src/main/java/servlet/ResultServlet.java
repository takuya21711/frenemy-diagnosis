package servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.CounterDAO;
import dao.ResultDAO;
import model.Result;

/**
 * 結果画面（{@code /result?id=<resultId>}）。
 *
 * <p>保存は {@link QuizServlet} が済ませているので、ここでは
 * {@link ResultDAO#findById} で読んで JSP に流すだけ。
 * これにより結果 URL は冪等になり、ブラウザのリロードで二重保存されない。
 *
 * <p>JSP に渡す属性:
 * <ul>
 *   <li>{@code result} — 結果本体</li>
 *   <li>{@code shareUrl} — X(旧Twitter) の投稿画面 URL</li>
 *   <li>{@code count} — これまでの診断人数（{@link CounterDAO#count}）</li>
 * </ul>
 */
@WebServlet("/result")
public class ResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Result result = findResult(request);
		if (result == null) {
			WebUtils.forwardError(request, response, "診断結果が見つかりませんでした。",
					HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		//X(旧Twitter)の共有リンク
		String resultUrl = request.getRequestURL() + "?id=" + result.getResultId();
		String roleName = result.getRole().getName();
		String text = (result.getRole().isBlack() ? "お前は「" + roleName + "」だ！" : "私の役職は「" + roleName + "」でした！")
				+ " #フレネミー診断";
		String shareUrl = "https://x.com/intent/post?text=" + encode(text) + "&url=" + encode(resultUrl);

		request.setAttribute("result", result);
		request.setAttribute("shareUrl", shareUrl);
		request.setAttribute("count", new CounterDAO().count());

		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
		dispatcher.forward(request, response);
	}

	/** ?id= の結果を読む。id が不正・見つからなければ null */
	static Result findResult(HttpServletRequest request) {
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			return new ResultDAO().findById(id);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String encode(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}
}
