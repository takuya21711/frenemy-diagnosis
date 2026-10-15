package servlet;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import model.Answer;
import model.Choice;
import model.Question;

/**
 * 診断中のセッション状態（出題リスト・今何問目か・回答・選択肢）を1箇所に閉じたラッパー。
 *
 * <p>目的
 * <ul>
 *   <li>{@code (List&lt;Question&gt;) session.getAttribute(...)} の
 *       キャストと {@code @SuppressWarnings("unchecked")} を QuizServlet から追い出す</li>
 *   <li>セッションキー文字列の直接参照をなくし、意味のあるメソッド名で扱う</li>
 * </ul>
 *
 * <p>並行アクセスの制御（連打対策の {@code synchronized}）は呼び出し側（QuizServlet）の責務。
 * このクラス自体はスレッドセーフにしない。
 */
final class QuizSession {
	private final HttpSession session;

	private QuizSession(HttpSession session) {
		this.session = session;
	}

	/**
	 * 既存セッションが無ければ {@code null} を返す（スタート前に /quiz を叩かれたケースなど）
	 */
	static QuizSession of(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session == null ? null : new QuizSession(session);
	}

	/** 新規作成または既存セッションを取得する（start 用） */
	static QuizSession create(HttpServletRequest request) {
		return new QuizSession(request.getSession());
	}

	/** 元の {@link HttpSession}（{@code synchronized} 用のモニタとして使う） */
	HttpSession raw() {
		return session;
	}

	/** 診断中かどうか（出題リストがセッションに乗っているか） */
	boolean isStarted() {
		return getQuizList() != null;
	}

	@SuppressWarnings("unchecked")
	List<Question> getQuizList() {
		return (List<Question>) session.getAttribute(SessionKeys.QUIZ_LIST);
	}

	@SuppressWarnings("unchecked")
	List<Answer> getAnswerList() {
		return (List<Answer>) session.getAttribute(SessionKeys.ANSWER_LIST);
	}

	@SuppressWarnings("unchecked")
	List<Choice> getChoices() {
		return (List<Choice>) session.getAttribute(SessionKeys.CHOICES);
	}

	int getIndex() {
		Integer v = (Integer) session.getAttribute(SessionKeys.QUIZ_INDEX);
		return v == null ? 0 : v;
	}

	/** 診断を開始する（出題リスト・選択肢をセットし、回答を空にする） */
	void start(List<Question> quizList, List<Choice> choices) {
		session.setAttribute(SessionKeys.QUIZ_LIST, quizList);
		session.setAttribute(SessionKeys.QUIZ_INDEX, 0);
		session.setAttribute(SessionKeys.ANSWER_LIST, new ArrayList<Answer>());
		session.setAttribute(SessionKeys.CHOICES, choices);
	}

	/** 今の問題（0オリジンの index で引く） */
	Question currentQuestion() {
		return getQuizList().get(getIndex());
	}

	/** 回答を1件追加して、インデックスを進める */
	void addAnswer(Answer answer) {
		getAnswerList().add(answer);
		session.setAttribute(SessionKeys.QUIZ_INDEX, getIndex() + 1);
	}

	/** 直前の回答を取り消して、インデックスを1戻す */
	void undoLastAnswer() {
		List<Answer> answers = getAnswerList();
		int index = getIndex();
		if (answers != null && index > 0 && !answers.isEmpty()) {
			answers.remove(answers.size() - 1);
			session.setAttribute(SessionKeys.QUIZ_INDEX, index - 1);
		}
	}

	/** 直前の回答だけを取り消す（インデックスは動かさない：結果保存失敗時のリトライ用） */
	void removeLastAnswer() {
		List<Answer> answers = getAnswerList();
		if (answers != null && !answers.isEmpty()) {
			answers.remove(answers.size() - 1);
		}
	}

	/** 診断中のデータを全部消す */
	void clear() {
		session.removeAttribute(SessionKeys.QUIZ_LIST);
		session.removeAttribute(SessionKeys.QUIZ_INDEX);
		session.removeAttribute(SessionKeys.ANSWER_LIST);
		session.removeAttribute(SessionKeys.CHOICES);
	}
}
