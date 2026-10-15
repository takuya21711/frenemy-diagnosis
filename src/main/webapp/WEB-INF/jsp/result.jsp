<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- 黒の役職は「お前は〜だ」＋怖い背景 --%>
<c:set var="black" value="${result.role.black}" />
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><c:out value="${result.role.name}" /> | 診断結果 | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
<script src="${pageContext.request.contextPath}/js/result-image.js" defer></script>
</head>
<body class="flex min-h-screen flex-col ${black ? 'bg-[radial-gradient(circle_at_center,#5a0000,#0d0000)] text-[#f5e6e6]' : 'bg-[#fffdf0] text-[#2b2b3a]'}">
  <%@ include file="/WEB-INF/jsp/common/header.jsp" %>

  <main class="mx-auto flex w-full max-w-xl flex-1 flex-col gap-6 px-4 py-8 sm:py-12">
    <!-- 役職（画像保存ボタンが、この中の data-* を使って画像を描く） -->
    <section id="resultCard"
             data-headline="${black ? 'お前は' : 'あなたの役職は'}"
             data-suffix="${black ? 'だ' : 'です'}"
             data-role="<c:out value='${result.role.name}' />"
             data-image="${pageContext.request.contextPath}/<c:out value='${result.imagePath}' />"
             data-item="<c:out value='${result.itemName}' />"
             data-black="${black}"
             class="rounded-2xl border-[3px] p-6 text-center sm:p-8 ${black ? 'border-[#e0301e] bg-[#1a0505]' : 'border-[#3f51c2] bg-white shadow-[4px_4px_0_#3f51c2]'}">
      <c:if test="${not empty result.imagePath}">
        <img src="${pageContext.request.contextPath}/<c:out value='${result.imagePath}' />" alt=""
             class="mx-auto h-40 w-40 rounded-full border-[3px] object-cover ${black ? 'border-[#e0301e]' : 'border-[#3f51c2]'}">
      </c:if>
      <h2 class="mt-4 text-xl font-bold sm:text-2xl">
        <c:choose>
          <c:when test="${black}">お前は<br><span class="text-4xl font-black text-[#e0301e] sm:text-5xl">「<c:out value="${result.role.name}" />」</span><br>だ</c:when>
          <c:otherwise>あなたの役職は<br><span class="text-4xl font-black text-[#3f51c2] sm:text-5xl">「<c:out value="${result.role.name}" />」</span><br>です</c:otherwise>
        </c:choose>
      </h2>
      <p class="mt-4 text-left leading-relaxed"><c:out value="${result.role.description}" /></p>
    </section>

    <!-- スコア（クリティカル吉嵜のときは無い） -->
    <c:if test="${not empty result.traits}">
      <section aria-labelledby="scoreTitle">
        <h3 id="scoreTitle" class="mb-3 font-bold">スコア（-30 〜 +30）</h3>
        <ul id="scoreList" class="flex flex-col gap-2">
          <c:forEach var="trait" items="${result.traits}">
            <li data-name="<c:out value='${trait.name}' />" data-score="${trait.score}">
              <div class="flex justify-between text-sm font-bold">
                <span><c:out value="${trait.name}" /></span>
                <span>${trait.score}</span>
              </div>
              <div class="h-3 w-full overflow-hidden rounded-full border-2 ${black ? 'border-[#e0301e] bg-black' : 'border-[#3f51c2] bg-white'}"
                   role="meter" aria-valuemin="-30" aria-valuemax="30" aria-valuenow="${trait.score}"
                   aria-label="<c:out value='${trait.name}' />">
                <div class="h-full ${black ? 'bg-[#e0301e]' : 'bg-[#f9dc33]'}" style="width: ${trait.percent}%"></div>
              </div>
            </li>
          </c:forEach>
        </ul>
      </section>
    </c:if>

    <!-- おすすめアイテム -->
    <c:if test="${not empty result.itemName}">
      <section class="rounded-xl border-2 p-4 ${black ? 'border-[#e0301e]' : 'border-[#3f51c2] bg-white'}">
        <h3 class="text-sm font-bold">おすすめのアイテム</h3>
        <p class="mt-1 text-lg font-bold"><c:out value="${result.itemName}" /></p>
      </section>
    </c:if>

    <!-- ボタン -->
    <div class="flex flex-col gap-3">
      <button type="button" id="saveImageButton"
              class="w-full cursor-pointer rounded-full border-[3px] border-[#3f51c2] bg-white py-3 font-bold text-[#3f51c2] hover:bg-[#f9dc33]">画像にして保存する</button>
      <a href="<c:out value='${shareUrl}' />" target="_blank" rel="noopener noreferrer"
         class="w-full rounded-full bg-black py-3 text-center font-bold text-white hover:opacity-85">Xで共有する</a>
      <form action="${pageContext.request.contextPath}/quiz" method="post">
        <input type="hidden" name="action" value="start">
        <button type="submit"
                class="w-full cursor-pointer rounded-full bg-[#3f51c2] py-3 font-bold text-white hover:opacity-85">もう一度診断する</button>
      </form>
      <div class="flex gap-3">
        <a href="${pageContext.request.contextPath}/"
           class="flex-1 rounded-full border-[3px] border-[#3f51c2] bg-white py-2.5 text-center font-bold text-[#3f51c2] hover:bg-[#f9dc33]">トップへ</a>
        <a href="${pageContext.request.contextPath}/roles"
           class="flex-1 rounded-full border-[3px] border-[#3f51c2] bg-white py-2.5 text-center font-bold text-[#3f51c2] hover:bg-[#f9dc33]">役職一覧</a>
      </div>
    </div>

    <c:if test="${count >= 0}">
      <p class="text-center text-sm opacity-70">これまでに ${count} 人が診断しました</p>
    </c:if>
  </main>

  <%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
</body>
</html>
