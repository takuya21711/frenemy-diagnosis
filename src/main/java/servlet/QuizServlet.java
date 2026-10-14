package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.QuestionDAO;
import model.Answer;
import model.Choice;
import model.Question;

/**
 * 診断画面
 * GET  : 今の問題を表示
 * POST : action=start  診断開始（出題リストを作ってセッションに保存）
 *        action=answer 回答を受け取って次の問題へ
 */
@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** 「とても当てはまる」の choice_id（吉嵜質問でこれを選ぶとクリティカル） */
	private static final int CRITICAL_CHOICE_ID = 1;

	//セッションのキー
	static final String QUIZ_LIST = "quizList";
	static final String QUIZ_INDEX = "quizIndex";
	static final String ANSWER_LIST = "answerList";
	static final String CHOICES = "choices";
	static final String CRITICAL = "critical";

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		List<Question> quizList = session == null ? null
				: (List<Question>) session.getAttribute(QUIZ_LIST);

		//診断を始めていなければ開始画面へ
		if (quizList == null) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		int index = (Integer) session.getAttribute(QUIZ_INDEX);
		if (index >= quizList.size()) {
			response.sendRedirect(request.getContextPath() + "/result");
			return;
		}

		//今の問題と進捗をリクエストスコープに保存（選択肢はセッションにある）
		request.setAttribute("question", quizList.get(index));
		request.setAttribute("number", index + 1);
		request.setAttribute("total", quizList.size());
		request.setAttribute("progress", index * 100 / quizList.size());

		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/quiz.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");

		if ("start".equals(action)) {
			start(request, response);
		} else if ("answer".equals(action)) {
			answer(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/quiz");
		}
	}

	/** 診断開始：前回の回答を消して、出題リストを作る */
	private void start(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		clearQuiz(session);
		session.removeAttribute(CRITICAL);

		QuestionDAO dao = new QuestionDAO();
		List<Question> quizList = dao.findQuiz();
		List<Choice> choices = dao.findChoices();
		if (quizList.isEmpty() || choices.isEmpty()) {
			request.setAttribute("errorMessage", "質問を読み込めませんでした。時間をおいてもう一度お試しください。");
			RequestDispatcher dispatcher =
					request.getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			dispatcher.forward(request, response);
			return;
		}

		session.setAttribute(QUIZ_LIST, quizList);
		session.setAttribute(QUIZ_INDEX, 0);
		session.setAttribute(ANSWER_LIST, new ArrayList<Answer>());
		session.setAttribute(CHOICES, choices);

		//リロードで再開始されないようにリダイレクト
		response.sendRedirect(request.getContextPath() + "/quiz");
	}

	/** 回答：画面から来るのは questionId と choiceId だけ（点数は受け取らない） */
	@SuppressWarnings("unchecked")
	private void answer(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute(QUIZ_LIST) == null) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		List<Question> quizList = (List<Question>) session.getAttribute(QUIZ_LIST);
		List<Answer> answerList = (List<Answer>) session.getAttribute(ANSWER_LIST);
		List<Choice> choices = (List<Choice>) session.getAttribute(CHOICES);
		int index = (Integer) session.getAttribute(QUIZ_INDEX);

		if (index >= quizList.size()) {
			response.sendRedirect(request.getContextPath() + "/result");
			return;
		}

		int questionId;
		int choiceId;
		try {
			questionId = Integer.parseInt(request.getParameter("questionId"));
			choiceId = Integer.parseInt(request.getParameter("choiceId"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/quiz");
			return;
		}

		//「今の問題」と違う（戻るボタン・二重送信など）か、ありえない選択肢なら無視して今の問題を表示
		Question current = quizList.get(index);
		if (current.getQuestionId() != questionId || !isValidChoice(choices, choiceId)) {
			response.sendRedirect(request.getContextPath() + "/quiz");
			return;
		}

		//クリティカル吉嵜：その場で診断終了
		if (current.isCritical() && choiceId == CRITICAL_CHOICE_ID) {
			clearQuiz(session);
			session.setAttribute(CRITICAL, true);
			response.sendRedirect(request.getContextPath() + "/critical");
			return;
		}

		answerList.add(new Answer(questionId, choiceId));
		index++;
		session.setAttribute(QUIZ_INDEX, index);

		if (index >= quizList.size()) {
			response.sendRedirect(request.getContextPath() + "/result");
		} else {
			response.sendRedirect(request.getContextPath() + "/quiz");
		}
	}

	private boolean isValidChoice(List<Choice> choices, int choiceId) {
		for (Choice choice : choices) {
			if (choice.getChoiceId() == choiceId) {
				return true;
			}
		}
		return false;
	}

	/** セッションから診断中のデータを消す */
	static void clearQuiz(HttpSession session) {
		session.removeAttribute(QUIZ_LIST);
		session.removeAttribute(QUIZ_INDEX);
		session.removeAttribute(ANSWER_LIST);
		session.removeAttribute(CHOICES);
	}
}
