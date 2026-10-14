<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="model.User" %>
<% User loginUser = (User)session.getAttribute("loginUser"); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>フレネミー診断</title>
</head>
<body>
<h1>フレネミー診断ログイン</h1>
<% if (loginUser != null){ %>
	<p>ログインに成功</p>
	<a href="start">フレネミー診断へ</a>
<% }else{ %>
	<p>ログインに失敗しました</p>
	<a href="login.jsp">トップへ</a>
<% } %>
</body>
</html>