<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>エラー | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="min-h-screen bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto w-full max-w-xl px-4 py-12 text-center">
    <h2 class="text-2xl font-bold text-[#3f51c2]">エラーが発生しました</h2>
    <p class="mt-4"><c:out value="${errorMessage}" default="予期しないエラーが発生しました。" /></p>
    <a href="${pageContext.request.contextPath}/StartServlet"
       class="mt-8 inline-block rounded-xl bg-[#3f51c2] px-8 py-3 font-bold text-white hover:opacity-85">トップに戻る</a>
  </main>
</body>
</html>
