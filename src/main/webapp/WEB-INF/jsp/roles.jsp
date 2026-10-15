<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>役職一覧 | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body class="flex min-h-screen flex-col bg-[#fffdf0] text-[#2b2b3a]">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto w-full max-w-3xl flex-1 px-4 py-8 sm:py-12">
    <h2 class="text-center text-2xl font-bold text-[#3f51c2]">役職一覧</h2>
    <p class="mt-2 text-center text-sm text-gray-500">全${roles.size()}種類</p>

    <ul class="mt-6 flex flex-col gap-4">
      <c:forEach var="role" items="${roles}">
        <%-- 吉嵜は自分で出すまで「？？？」 --%>
        <c:set var="locked" value="${role.yoshizaki and not yoshizakiUnlocked}" />
        <li class="flex gap-4 rounded-2xl border-[3px] bg-white p-4 ${role.black ? 'border-[#e0301e]' : 'border-[#3f51c2]'}">
          <c:choose>
            <c:when test="${locked or empty role.imagePath}">
              <div class="flex h-20 w-20 shrink-0 items-center justify-center rounded-full border-[3px] border-gray-400 bg-gray-200 text-3xl font-black text-gray-500" aria-hidden="true">？</div>
            </c:when>
            <c:otherwise>
              <img src="${pageContext.request.contextPath}/<c:out value='${role.imagePath}' />" alt=""
                   class="h-20 w-20 shrink-0 rounded-full border-[3px] object-cover ${role.black ? 'border-[#e0301e]' : 'border-[#3f51c2]'}">
            </c:otherwise>
          </c:choose>
          <div>
            <h3 class="text-lg font-bold">
              <c:choose>
                <c:when test="${locked}">？？？</c:when>
                <c:otherwise><c:out value="${role.name}" /></c:otherwise>
              </c:choose>
              <c:if test="${role.black and not locked}"><span class="ml-2 rounded bg-[#e0301e] px-2 py-0.5 text-xs text-white">黒</span></c:if>
            </h3>
            <p class="mt-1 text-sm leading-relaxed">
              <c:choose>
                <c:when test="${locked}">ある条件を満たすと解放されます……。</c:when>
                <c:otherwise><c:out value="${role.description}" /></c:otherwise>
              </c:choose>
            </p>
          </div>
        </li>
      </c:forEach>
    </ul>
  </main>

  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
</body>
</html>
