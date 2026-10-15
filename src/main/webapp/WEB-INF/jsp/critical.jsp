<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>クリティカル吉嵜 | フレネミー診断</title>
<script src="${pageContext.request.contextPath}/webjars/tailwindcss__browser/4.1.8/dist/index.global.js"></script>
<script src="${pageContext.request.contextPath}/js/explosion.js" defer></script>
<style>
  /* 画面が揺れる */
  @keyframes shake {
    0%, 100% { transform: translate(0, 0); }
    10% { transform: translate(-14px, 8px); }
    20% { transform: translate(12px, -10px); }
    30% { transform: translate(-10px, -6px); }
    40% { transform: translate(8px, 10px); }
    55% { transform: translate(-6px, 4px); }
    70% { transform: translate(4px, -3px); }
    85% { transform: translate(-2px, 2px); }
  }
  body.shake { animation: shake 0.7s ease-out; }

  /* 白く光る */
  @keyframes flash {
    from { opacity: 1; }
    to   { opacity: 0; }
  }
  #flash.flash { animation: flash 0.6s ease-out forwards; }

  /* 吉嵜が飛び出す */
  #reveal { opacity: 0; transform: scale(0.6); transition: opacity 0.5s, transform 0.6s cubic-bezier(0.2, 1.6, 0.4, 1); }
  #reveal.is-shown { opacity: 1; transform: scale(1); }
</style>
</head>
<body class="min-h-screen overflow-x-hidden bg-[#1a0a00] text-[#2b2b3a]">
  <canvas id="explosion" class="pointer-events-none fixed inset-0 z-20 h-full w-full" aria-hidden="true"></canvas>
  <div id="flash" class="pointer-events-none fixed inset-0 z-30 bg-white opacity-0" aria-hidden="true"></div>

  <main class="flex min-h-screen items-center justify-center px-4 py-10">
    <section id="reveal" hidden aria-live="polite"
             class="w-full max-w-md rounded-2xl border-[3px] border-[#3f51c2] bg-[#fffdf0] p-6 text-center shadow-[6px_6px_0_#f9dc33] sm:p-8">
      <p class="text-lg font-black tracking-widest text-[#e0301e]">CRITICAL!!</p>
      <p class="mt-1 text-sm font-bold text-[#9583db]">クリティカル吉嵜 発動</p>

      <img src="${pageContext.request.contextPath}/<c:out value="${result.imagePath}" />" alt=""
           class="mx-auto mt-6 h-40 w-40 rounded-full border-[3px] border-[#3f51c2] bg-[#f9dc33] object-cover">

      <h1 class="mt-6 text-2xl font-black sm:text-3xl">あなたの役職は<br><span class="text-[#e0301e]">「<c:out value="${result.role.name}" />」</span>です</h1>
      <p class="mt-3 text-sm text-gray-500">診断はここで終了しました。</p>

      <div class="mt-8 flex flex-col gap-3">
        <a href="${pageContext.request.contextPath}/result?id=${result.resultId}"
           class="block w-full rounded-xl bg-[#e0301e] py-3 font-bold text-white hover:opacity-85">結果を見る</a>
        <form action="${pageContext.request.contextPath}/quiz" method="post">
          <input type="hidden" name="action" value="start">
          <button type="submit"
                  class="w-full cursor-pointer rounded-xl bg-[#3f51c2] py-3 font-bold text-white hover:opacity-85">もう一度診断する</button>
        </form>
        <a href="${pageContext.request.contextPath}/StartServlet"
           class="block w-full rounded-xl border-[3px] border-[#3f51c2] bg-white py-2.5 font-bold text-[#3f51c2] hover:bg-[#f9dc33]">トップに戻る</a>
      </div>
    </section>
  </main>
</body>
</html>
