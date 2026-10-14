<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Q${number} | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="min-h-screen bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto w-full max-w-xl px-4 py-8 sm:py-12">
    <!-- 進捗 -->
    <section aria-label="進捗">
      <div class="mb-2 flex justify-between text-sm font-bold text-[#3f51c2]">
        <span>${number} / ${total} 問</span>
        <span>${progress}%</span>
      </div>
      <div class="h-3 w-full overflow-hidden rounded-full border-2 border-[#3f51c2] bg-white"
           role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="${progress}"
           aria-label="診断の進み具合">
        <div class="h-full bg-[#f9dc33]" style="width: ${progress}%"></div>
      </div>
    </section>

    <!-- 質問 -->
    <section class="mt-8 rounded-2xl border-[3px] border-[#3f51c2] bg-white p-6 shadow-[4px_4px_0_#3f51c2] sm:p-8">
      <p class="text-sm font-bold text-[#9583db]">Q${number}</p>
      <h2 id="questionText" class="mt-2 text-lg font-bold leading-relaxed sm:text-xl">
        <c:out value="${question.question}" />
      </h2>
    </section>

    <!-- 回答（送るのは質問IDと選択肢IDだけ） -->
    <form action="${pageContext.request.contextPath}/quiz" method="post"
          class="mt-6 flex flex-col gap-3" aria-labelledby="questionText">
      <input type="hidden" name="action" value="answer">
      <input type="hidden" name="questionId" value="${question.questionId}">
      <c:forEach var="choice" items="${choices}">
        <button type="submit" name="choiceId" value="${choice.choiceId}"
                class="w-full cursor-pointer rounded-xl border-[3px] border-[#3f51c2] bg-white px-4 py-4 text-base font-bold text-[#3f51c2] shadow-[3px_3px_0_#3f51c2] transition hover:bg-[#f9dc33] focus-visible:bg-[#f9dc33] focus-visible:outline-none active:translate-x-[2px] active:translate-y-[2px] active:shadow-none">
          <c:out value="${choice.label}" />
        </button>
      </c:forEach>
    </form>
  </main>
</body>
</html>
