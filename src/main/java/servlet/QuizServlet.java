package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.QuestionDAO;
import dao.ResultDAO;
import dao.RoleDAO;
import dao.ScoringDAO;
import model.Answer;
import model.Choice;
import model.Question;
import model.Role;
import model.RoleJudge;
import model.ScoreCalculator;
import model.Scores;
import model.User;

/**
 * 診断画面
 * GET  : 今の問題を表示
 * POST : action=start  診断開始（出題リストを作ってセッションに保存）
 *        action=answer 回答を受け取って次の問題へ（最後の問題なら結果を保存して結果画面へ）
 *        action=back   1問前に戻る
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

	/* 
	 * SuppressWarningsアノテーションは、コンパイラに警告を抑制するよう指示するために使用される
	 * Uncheckedはジェネリック型のキャストまたはインスタンス化に関連する警告を抑制します。
	 * 下のURLに記載ある
	 * https://qiita.com/kageryosan/items/3634009804b480bf953f
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// sessionの取り出し
		// getSessionでfalseにしている理由は、セッションがないときにnullを返したいから
		// スタートボタンを押さずに/quizを叩かれて、不正な診断を受けるのを防止するため
		HttpSession session = request.getSession(false);
		List<Question> quizList = session == null ? null
				: (List<Question>) session.getAttribute(QUIZ_LIST);

		//診断を始めていなければ開始画面へ
		if (quizList == null) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		int index = (Integer) session.getAttribute(QUIZ_INDEX);

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
		} else if ("back".equals(action)) {
			back(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/quiz");
		}
	}

	/** 診断開始：前回の回答を消して、出題リストを作る */
	private void start(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		clearQuiz(session);

		QuestionDAO dao = new QuestionDAO();
		List<Question> quizList = dao.findQuiz();
		List<Choice> choices = dao.findChoices();
		if (quizList.isEmpty() || choices.isEmpty()) {
			forwardError(request, response, "質問を読み込めませんでした。時間をおいてもう一度お試しください。");
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
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect(request.getContextPath() + "/StartServlet");
			return;
		}
		//ボタンの連打で同じ回答が2回処理されないように、セッションごとに順番に処理する
		synchronized (session) {
			List<Question> quizList = (List<Question>) session.getAttribute(QUIZ_LIST);
			if (quizList == null) {
				response.sendRedirect(request.getContextPath() + "/StartServlet");
				return;
			}
			List<Answer> answerList = (List<Answer>) session.getAttribute(ANSWER_LIST);
			List<Choice> choices = (List<Choice>) session.getAttribute(CHOICES);
			int index = (Integer) session.getAttribute(QUIZ_INDEX);

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

			//クリティカル吉嵜：その場で診断終了（スコアなしで吉嵜として保存）
			if (current.isCritical() && choiceId == CRITICAL_CHOICE_ID) {
				int resultId = new ResultDAO().insert(Role.YOSHIZAKI_ID, null, loginUserId(session));
				if (resultId < 0) {
					forwardError(request, response, "結果を保存できませんでした。もう一度お試しください。");
					return;
				}
				clearQuiz(session);
				YoshizakiUnlock.unlock(request, response);
				response.sendRedirect(request.getContextPath() + "/critical?id=" + resultId);
				return;
			}

			answerList.add(new Answer(questionId, choiceId));
			index++;

			if (index < quizList.size()) {
				session.setAttribute(QUIZ_INDEX, index);
				response.sendRedirect(request.getContextPath() + "/quiz");
				return;
			}

			//全問回答した：スコア計算 → 役職判定 → 保存 → 結果画面
			int resultId = saveResult(quizList, answerList, loginUserId(session));
			if (resultId < 0) {
				//最後の回答を取り消して、もう一度送れるようにしておく
				answerList.remove(answerList.size() - 1);
				forwardError(request, response, "結果を保存できませんでした。もう一度お試しください。");
				return;
			}
			clearQuiz(session);
			//結果画面はIDで表示するので、リロードしても二重に保存されない
			response.sendRedirect(request.getContextPath() + "/result?id=" + resultId);
		}
	}

	/** 1問前に戻る（直前の回答を取り消す） */
	@SuppressWarnings("unchecked")
	private void back(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			synchronized (session) {
				List<Answer> answerList = (List<Answer>) session.getAttribute(ANSWER_LIST);
				Integer index = (Integer) session.getAttribute(QUIZ_INDEX);
				if (answerList != null && index != null && index > 0 && !answerList.isEmpty()) {
					answerList.remove(answerList.size() - 1);
					session.setAttribute(QUIZ_INDEX, index - 1);
				}
			}
		}
		response.sendRedirect(request.getContextPath() + "/quiz");
	}

	/** スコアを計算して役職を決め、結果テーブルに保存する。失敗したら -1 */
	private int saveResult(List<Question> quizList, List<Answer> answerList, Integer userId) {
		ScoringDAO scoringDAO = new ScoringDAO();
		Map<Integer, Map<Integer, Integer>> scoring = scoringDAO.findScoring(answerList);
		Map<Integer, Integer> fixedScores = scoringDAO.findFixedScores(answerList);
		List<Role> roles = new RoleDAO().findAll();
		if (scoring == null || fixedScores == null || roles.isEmpty()) {
			return -1;
		}

		Scores scores = new ScoreCalculator().calculate(quizList, answerList, scoring, fixedScores);
		Role role = new RoleJudge().judge(roles, scores);
		if (role == null) {
			return -1;
		}
		return new ResultDAO().insert(role.getId(), scores, userId);
	}

	private Integer loginUserId(HttpSession session) {
		User loginUser = (User) session.getAttribute(Login.LOGIN_USER);
		return loginUser == null ? null : loginUser.getUserId();
	}

	private boolean isValidChoice(List<Choice> choices, int choiceId) {
		for (Choice choice : choices) {
			if (choice.getChoiceId() == choiceId) {
				return true;
			}
		}
		return false;
	}

	private void forwardError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("errorMessage", message);
		RequestDispatcher dispatcher =
				request.getRequestDispatcher("/WEB-INF/jsp/error.jsp");
		dispatcher.forward(request, response);
	}

	/** セッションから診断中のデータを消す */
	static void clearQuiz(HttpSession session) {
		session.removeAttribute(QUIZ_LIST);
		session.removeAttribute(QUIZ_INDEX);
		session.removeAttribute(ANSWER_LIST);
		session.removeAttribute(CHOICES);
	}
}
