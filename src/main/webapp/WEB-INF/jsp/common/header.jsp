<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<header class="sticky top-0 z-50 flex items-center gap-4 border-b-4 border-[#3f51c2] bg-[#9583db] px-3 py-2 sm:px-6">
	<h1 class="z-0">
	<!-- アプリのプロジェクト名（ルートパス）は、${pageContext.request.contextPath}でとれるらしい -->
		<a class="flex items-center gap-2 text-2xl font-bold text-white [text-shadow:2px_2px_0_#3f51c2]" href="${pageContext.request.contextPath}/">
			<img class="h-12 w-12 scale-125 object-cover overflow-hidden rounded-full" src="${pageContext.request.contextPath}/image/frenemy-log.png" alt="">
		</a>
	</h1>
	<!-- 画面遷移用 -->
	<section class="ml-auto">
		<a class="rounded-full border-2 border-[#3f51c2] bg-white px-4 py-1.5 text-sm font-bold text-[#3f51c2] hover:bg-[#f9dc33] sm:text-base" href="${pageContext.request.contextPath}/">トップに戻る</a>
	</section>
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
			<%-- TODO: ログイン機能ができたら session のユーザー名に置き換える（今はモック） --%>
			<p class="mb-3 font-bold text-[#3f51c2]">モック ユーザー さん</p>
			<%-- TODO: ログアウト処理ができたら LogoutServlet へ送る（今はモック） --%>
			<button type="button" id="logoutButton"
					class="w-full cursor-pointer rounded-lg bg-[#3f51c2] py-2 font-bold text-white hover:opacity-85">ログアウト</button>
		</div>
	</section>
</header>
