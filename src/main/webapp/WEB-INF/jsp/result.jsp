<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>診断結果 | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="min-h-screen bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <%-- TODO: 結果画面（仮）。スコア計算・役職判定ができたら作り直す --%>
  <main class="mx-auto w-full max-w-xl px-4 py-12 text-center">
    <h2 class="text-2xl font-bold text-[#3f51c2]">診断おつかれさまでした</h2>
    <p class="mt-4">${answerCount} 問に回答しました。結果画面は準備中です。</p>
    <form action="${pageContext.request.contextPath}/quiz" method="post" class="mt-8">
      <input type="hidden" name="action" value="start">
      <button type="submit"
              class="cursor-pointer rounded-xl bg-[#3f51c2] px-8 py-3 font-bold text-white hover:opacity-85">もう一度診断する</button>
    </form>
  </main>
</body>
</html>
