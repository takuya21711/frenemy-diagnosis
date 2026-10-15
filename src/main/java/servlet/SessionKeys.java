package servlet;

/**
 * {@link jakarta.servlet.http.HttpSession} に保存する属性のキー。
 * 文字列を直接書くとタイポに気付けない＆参照箇所を grep しづらいので、ここで一元管理する。
 */
final class SessionKeys {
	/** ログイン中のユーザー（{@link model.User}）。未ログインは属性なし */
	static final String LOGIN_USER = "loginUser";

	/** 診断の出題リスト（{@code List<model.Question>}） */
	static final String QUIZ_LIST = "quizList";

	/** 診断の今何問目か（{@code Integer}、0オリジン） */
	static final String QUIZ_INDEX = "quizIndex";

	/** これまでの回答（{@code List<model.Answer>}） */
	static final String ANSWER_LIST = "answerList";

	/** 選択肢（{@code List<model.Choice>}。4段階） */
	static final String CHOICES = "choices";

	private SessionKeys() {
	}
}
