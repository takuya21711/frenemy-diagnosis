<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag pageEncoding="UTF-8" %>
<%-- 親から受け取る属性（パラメータ）の定義 --%>
<%@ attribute name="name" required="true" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="imageUrl" required="true" %>

<!-- 1枚のカードのコンポーネント本体 -->
<div class="card">
    <!-- 左側：画像 -->
    <div class="card-image-box">
        <c:choose>
            <c:when test="${not empty imageUrl}">
                <img src="${pageContext.request.contextPath}/${imageUrl}" alt="${name}">
            </c:when>
            <c:otherwise>
                <span class="image-placeholder">画像</span>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- 右側：名前と説明文 -->
    <div class="card-info-box">
        <div class="card-name">${name}</div>
        <div class="card-description">${description}</div>
    </div>
</div>