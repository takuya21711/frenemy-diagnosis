// クリティカル吉嵜：画面を爆発させてから吉嵜を表示する
document.addEventListener("DOMContentLoaded", () => {
  const canvas = document.getElementById("explosion");
  const flash = document.getElementById("flash");
  const reveal = document.getElementById("reveal");

  let shown = false;
  const showYoshizaki = () => {
    if (shown) {
      return;
    }
    shown = true;
    reveal.hidden = false;
    // hidden を外した次のフレームでクラスを付けないとアニメーションしない
    requestAnimationFrame(() => reveal.classList.add("is-shown"));
  };

  // 動きを減らす設定の人や canvas が使えない環境では、すぐ表示する
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (reduceMotion || !canvas || !canvas.getContext) {
    showYoshizaki();
    return;
  }

  const ctx = canvas.getContext("2d");
  const resize = () => {
    const dpr = window.devicePixelRatio || 1;
    canvas.width = window.innerWidth * dpr;
    canvas.height = window.innerHeight * dpr;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  };
  resize();
  window.addEventListener("resize", resize);

  const width = () => window.innerWidth;
  const height = () => window.innerHeight;
  const cx = width() / 2;
  const cy = height() / 2;
  const maxRadius = Math.hypot(width(), height()) / 2;

  // 火花
  const colors = ["#ff3b1f", "#ff8a00", "#f9dc33", "#ffffff", "#9583db"];
  const particles = Array.from({ length: 220 }, () => {
    const angle = Math.random() * Math.PI * 2;
    const speed = 4 + Math.random() * 16;
    return {
      x: cx,
      y: cy,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed,
      size: 2 + Math.random() * 6,
      life: 1,
      decay: 0.008 + Math.random() * 0.015,
      color: colors[Math.floor(Math.random() * colors.length)],
    };
  });

  let shockwave = 0;

  const draw = () => {
    // 少しずつ黒で塗って残像を出す
    ctx.globalAlpha = 1;
    ctx.fillStyle = "rgba(26, 10, 0, 0.25)";
    ctx.fillRect(0, 0, width(), height());

    // 衝撃波の輪
    if (shockwave < maxRadius) {
      shockwave += 20;
      ctx.beginPath();
      ctx.arc(cx, cy, shockwave, 0, Math.PI * 2);
      ctx.strokeStyle = `rgba(255, 220, 120, ${1 - shockwave / maxRadius})`;
      ctx.lineWidth = 8;
      ctx.stroke();
    }

    let alive = false;
    for (const p of particles) {
      if (p.life <= 0) {
        continue;
      }
      alive = true;
      p.x += p.vx;
      p.y += p.vy;
      p.vx *= 0.97;
      p.vy = p.vy * 0.97 + 0.15; // 重力
      p.life -= p.decay;

      ctx.globalAlpha = Math.max(p.life, 0);
      ctx.fillStyle = p.color;
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fill();
    }

    if (alive) {
      requestAnimationFrame(draw);
    } else {
      canvas.remove();
    }
  };

  document.body.classList.add("shake");
  flash.classList.add("flash");
  requestAnimationFrame(draw);

  // 火花が残っている間に吉嵜を出す
  setTimeout(showYoshizaki, 1200);
});
