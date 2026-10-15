<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>マイページ | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="flex min-h-screen flex-col bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto w-full max-w-xl flex-1 px-4 py-8 sm:py-12">
    <h2 class="text-2xl font-bold text-[#3f51c2]">マイページ</h2>
    <p class="mt-2">ユーザー名：<span class="font-bold"><c:out value="${sessionScope.loginUser.name}" /></span></p>

    <h3 class="mt-8 font-bold">診断履歴（${history.size()}件）</h3>
    <c:choose>
      <c:when test="${empty history}">
        <p class="mt-3 text-sm text-gray-500">まだ診断していません。</p>
        <form action="${pageContext.request.contextPath}/quiz" method="post" class="mt-4">
          <input type="hidden" name="action" value="start">
          <button type="submit"
                  class="cursor-pointer rounded-full bg-[#3f51c2] px-8 py-3 font-bold text-white hover:opacity-85">診断する</button>
        </form>
      </c:when>
      <c:otherwise>
        <ul class="mt-3 flex flex-col gap-2">
          <c:forEach var="r" items="${history}">
            <li>
              <a href="${pageContext.request.contextPath}/result?id=${r.resultId}"
                 class="flex items-center justify-between rounded-xl border-2 bg-white px-4 py-3 hover:bg-[#f9dc33] ${r.role.black ? 'border-[#e0301e]' : 'border-[#3f51c2]'}">
                <span class="font-bold"><c:out value="${r.role.name}" /></span>
                <span class="text-sm text-gray-500"><fmt:formatDate value="${r.createdAt}" pattern="yyyy/MM/dd HH:mm" /></span>
              </a>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </main>

  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
</body>
</html>
