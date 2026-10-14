<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>フレネミー診断</title>
<style>
	start-botton{
	}
</style>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/header.js" defer></script>
</head>
<body>
 <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<h2>フレネミー診断</h2>
<h3>この診断を受けた人数:${count}人</h3>
<form action="${pageContext.request.contextPath}/quiz" method="post">
	<input type="hidden" name="action" value="start">
	<input type="submit" value="スタート" class="start-botton">
</form>
<input type="submit" value="役職一覧" class="role-botton">
<h3>説明</h3>
<p>説明内容</p>
</body>
</html>