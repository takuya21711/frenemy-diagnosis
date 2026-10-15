package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * サーブレット間で共通して使うユーティリティ。
 */
final class WebUtils {

	private static final String ERROR_JSP = "/WEB-INF/jsp/error.jsp";

	private WebUtils() {
	}

	/**
	 * {@code errorMessage} をリクエストスコープに入れて error.jsp にフォワードする。
	 * HTTP ステータスは変えない。404 として返したい場合は {@link #forwardError(HttpServletRequest, HttpServletResponse, String, int)}
	 * を使う。
	 */
	static void forwardError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("errorMessage", message);
		RequestDispatcher dispatcher = request.getRequestDispatcher(ERROR_JSP);
		dispatcher.forward(request, response);
	}

	/**
	 * HTTP ステータスを指定してからフォワードする版（404 など）。
	 */
	static void forwardError(HttpServletRequest request, HttpServletResponse response,
			String message, int status) throws ServletException, IOException {
		response.setStatus(status);
		forwardError(request, response, message);
	}
}
