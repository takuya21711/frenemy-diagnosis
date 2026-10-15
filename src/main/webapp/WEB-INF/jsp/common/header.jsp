<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- スマホ幅ではハンバーガーメニュー、md(768px)以上では横並びのメニューになる（開閉は js/header.js） --%>
<header class="sticky top-0 z-50 flex items-center gap-4 border-b-4 border-[#3f51c2] bg-[#9583db] px-3 py-2 sm:px-6">
	<h1 class="z-0">
	<!-- アプリのプロジェクト名（ルートパス）は、${pageContext.request.contextPath}でとれるらしい -->
		<a class="flex items-center gap-2 text-2xl font-bold text-white [text-shadow:2px_2px_0_#3f51c2]" href="${pageContext.request.contextPath}/">
			<img class="h-12 w-12 scale-125 object-cover overflow-hidden rounded-full" src="${pageContext.request.contextPath}/image/frenemy-log.png" alt="フレネミー診断">
		</a>
	</h1>

	<!-- 画面遷移用。スマホでは閉じておき、ハンバーガーボタンで開く（md以上では常に表示） -->
	<nav id="globalNav" aria-label="メインメニュー"
		 class="absolute inset-x-0 top-full hidden border-b-4 border-[#3f51c2] bg-[#9583db] px-3 pb-3 md:static md:ml-auto md:block md:border-0 md:p-0">
		<ul class="flex flex-col gap-2 md:flex-row md:items-center">
			<li><a class="block rounded-full border-2 border-[#3f51c2] bg-white px-4 py-1.5 text-center text-sm font-bold text-[#3f51c2] hover:bg-[#f9dc33] sm:text-base" href="${pageContext.request.contextPath}/">トップに戻る</a></li>
			<li><a class="block rounded-full border-2 border-[#3f51c2] bg-white px-4 py-1.5 text-center text-sm font-bold text-[#3f51c2] hover:bg-[#f9dc33] sm:text-base" href="${pageContext.request.contextPath}/roles">役職一覧</a></li>
		</ul>
	</nav>

	<!-- ハンバーガーボタン（スマホのみ） -->
	<button type="button" id="menuButton"
			class="ml-auto flex h-8 w-8 cursor-pointer items-center justify-center rounded-lg border-[3px] border-[#3f51c2] bg-white hover:bg-[#f9dc33] aria-expanded:bg-[#f9dc33] md:hidden"
			aria-expanded="false" aria-controls="globalNav" aria-label="メニュー">
		<svg class="h-5 w-5 fill-none stroke-[#3f51c2] stroke-[2.5]" viewBox="0 0 24 24" stroke-linecap="round" aria-hidden="true">
			<path d="M4 6h16M4 12h16M4 18h16" />
		</svg>
	</button>

	<!-- アカウントアイコンとクリックしたら名前とログアウト表示 -->
	<section class="relative">
		<button type="button" id="accountButton"
				class="flex h-8 w-8 cursor-pointer items-center justify-center rounded-full border-[3px] border-[#3f51c2] bg-white hover:bg-[#f9dc33] aria-expanded:bg-[#f9dc33]"
				aria-haspopup="true" aria-expanded="false" aria-controls="accountMenu"
				aria-label="アカウント情報">
			<svg class="h-6 w-6 fill-none stroke-[#3f51c2] stroke-[2.5]" viewBox="0 0 24 24" stroke-linecap="round" aria-hidden="true">
				<circle cx="12" cy="8" r="4" />
				<path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7" />
			</svg>
		</button>

		<!-- JavaScriptで表示させるのでhidden属性が必要 -->
		<div id="accountMenu" hidden
			class="absolute right-0 top-[calc(100%+10px)] min-w-52 rounded-xl border-[3px] border-[#3f51c2] bg-white p-4 shadow-[4px_4px_0_#3f51c2]">
			<c:choose>
				<c:when test="${not empty sessionScope.loginUser}">
					<p class="mb-3 font-bold text-[#3f51c2]"><c:out value="${sessionScope.loginUser.name}" /> さん</p>
					<a href="${pageContext.request.contextPath}/mypage"
					   class="mb-2 block w-full rounded-lg border-2 border-[#3f51c2] py-1.5 text-center font-bold text-[#3f51c2] hover:bg-[#f9dc33]">マイページ</a>
					<form action="${pageContext.request.contextPath}/logout" method="post">
						<button type="submit"
								class="w-full cursor-pointer rounded-lg bg-[#3f51c2] py-2 font-bold text-white hover:opacity-85">ログアウト</button>
					</form>
				</c:when>
				<c:otherwise>
					<p class="mb-3 font-bold text-[#3f51c2]">ゲスト さん</p>
					<a href="${pageContext.request.contextPath}/Login"
					   class="mb-2 block w-full rounded-lg bg-[#3f51c2] py-2 text-center font-bold text-white hover:opacity-85">ログイン</a>
					<a href="${pageContext.request.contextPath}/register"
					   class="block w-full rounded-lg border-2 border-[#3f51c2] py-1.5 text-center font-bold text-[#3f51c2] hover:bg-[#f9dc33]">新規登録</a>
				</c:otherwise>
			</c:choose>
		</div>
	</section>
</header>
