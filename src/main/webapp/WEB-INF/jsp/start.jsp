<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="flex min-h-screen flex-col bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto flex w-full max-w-xl flex-1 flex-col items-center gap-6 px-4 py-10 text-center">
    <h2 class="text-3xl font-black text-[#3f51c2] sm:text-4xl">フレネミー診断</h2>

    <!-- カウンター（DBが読めないときは -1 が来る） -->
    <p class="rounded-xl border-[3px] border-[#3f51c2] bg-white px-6 py-3 font-bold">
      この診断を受けた人数：
      <c:choose>
        <c:when test="${count >= 0}"><span class="text-2xl text-[#3f51c2]">${count}</span> 人</c:when>
        <c:otherwise>―</c:otherwise>
      </c:choose>
    </p>

    <form action="${pageContext.request.contextPath}/quiz" method="post" class="w-full">
      <input type="hidden" name="action" value="start">
      <button type="submit"
              class="w-full cursor-pointer rounded-full bg-[#3f51c2] py-4 text-xl font-bold text-white hover:opacity-85">スタート</button>
    </form>
    <a href="${pageContext.request.contextPath}/roles"
       class="w-full rounded-full border-[3px] border-[#3f51c2] bg-white py-3 font-bold text-[#3f51c2] hover:bg-[#f9dc33]">役職一覧</a>

    <!-- 説明（文字を薄く表示） -->
    <section class="text-sm leading-relaxed text-gray-500">
      <h3 class="mb-2 font-bold">診断について</h3>
      <p>いくつかの質問に4段階で答えると、「外向性」「協調性」「誠実性」「情緒安定性」「開放性」の5つの性格特性を計算して、あなたに一番近い役職を診断します。</p>
      <p class="mt-2">ただし、質問の中には危険なものが混ざっているかもしれません……。</p>
    </section>
  </main>

  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
</body>
</html>
