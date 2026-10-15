<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ログイン | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="flex min-h-screen flex-col bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto w-full max-w-sm flex-1 px-4 py-10">
    <h2 class="text-center text-2xl font-bold text-[#3f51c2]">ログイン</h2>
    <p class="mt-2 text-center text-sm text-gray-500">ログインすると診断履歴が残ります。診断だけならログインは不要です。</p>

    <c:if test="${not empty errorMessage}">
      <p class="mt-4 rounded-lg border-2 border-[#e0301e] bg-white p-3 text-sm font-bold text-[#e0301e]" role="alert"><c:out value="${errorMessage}" /></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/Login" method="post" class="mt-6 flex flex-col gap-4">
      <label class="flex flex-col gap-1 font-bold">
        ユーザー名
        <input type="text" name="name" value="<c:out value='${name}' />" required autocomplete="username"
               class="rounded-lg border-2 border-[#3f51c2] bg-white px-3 py-2 font-normal">
      </label>
      <label class="flex flex-col gap-1 font-bold">
        パスワード
        <input type="password" name="pass" required autocomplete="current-password"
               class="rounded-lg border-2 border-[#3f51c2] bg-white px-3 py-2 font-normal">
      </label>
      <button type="submit"
              class="mt-2 cursor-pointer rounded-full bg-[#3f51c2] py-3 font-bold text-white hover:opacity-85">ログイン</button>
    </form>

    <p class="mt-6 text-center text-sm">
      アカウントが無い方は <a href="${pageContext.request.contextPath}/register" class="font-bold text-[#3f51c2] underline">新規登録</a>
    </p>
  </main>

  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
</body>
</html>
