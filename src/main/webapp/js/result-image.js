// 結果画面の「画像にして保存する」
// #resultCard の data-* と #scoreList を読んで canvas に描き、PNG としてダウンロードする
// （html2canvas は Tailwind v4 の色指定(oklch)を読めないので、自分で描いている）
document.addEventListener("DOMContentLoaded", () => {
  const button = document.getElementById("saveImageButton");
  const card = document.getElementById("resultCard");
  if (!button || !card) {
    return;
  }

  const WIDTH = 600;
  const HEIGHT = 800;

  const loadImage = (src) =>
    new Promise((resolve) => {
      const img = new Image();
      img.onload = () => resolve(img);
      img.onerror = () => resolve(null); // 画像が無くても文字だけで保存する
      img.src = src;
    });

  const draw = async () => {
    const d = card.dataset;
    const black = d.black === "true";
    const main = black ? "#e0301e" : "#3f51c2";
    const text = black ? "#f5e6e6" : "#2b2b3a";

    const canvas = document.createElement("canvas");
    canvas.width = WIDTH;
    canvas.height = HEIGHT;
    const ctx = canvas.getContext("2d");

    // 背景と枠
    ctx.fillStyle = black ? "#1a0505" : "#fffdf0";
    ctx.fillRect(0, 0, WIDTH, HEIGHT);
    ctx.strokeStyle = main;
    ctx.lineWidth = 8;
    ctx.strokeRect(12, 12, WIDTH - 24, HEIGHT - 24);

    ctx.textAlign = "center";
    ctx.fillStyle = "#9583db";
    ctx.font = "bold 24px sans-serif";
    ctx.fillText("フレネミー診断", WIDTH / 2, 64);

    // 役職画像（丸く切り抜く）
    const img = await loadImage(d.image);
    if (img) {
      ctx.save();
      ctx.beginPath();
      ctx.arc(WIDTH / 2, 190, 90, 0, Math.PI * 2);
      ctx.clip();
      ctx.drawImage(img, WIDTH / 2 - 90, 100, 180, 180);
      ctx.restore();
    }

    ctx.fillStyle = text;
    ctx.font = "bold 26px sans-serif";
    ctx.fillText(d.headline, WIDTH / 2, 330);
    ctx.fillStyle = main;
    ctx.font = "900 56px sans-serif";
    ctx.fillText("「" + d.role + "」", WIDTH / 2, 400);
    ctx.fillStyle = text;
    ctx.font = "bold 26px sans-serif";
    ctx.fillText(d.suffix, WIDTH / 2, 440);

    // スコアのバー
    const rows = document.querySelectorAll("#scoreList li");
    let y = 490;
    ctx.textAlign = "left";
    rows.forEach((li) => {
      const score = Number(li.dataset.score);
      ctx.fillStyle = text;
      ctx.font = "bold 18px sans-serif";
      ctx.fillText(li.dataset.name, 60, y + 16);
      ctx.textAlign = "right";
      ctx.fillText(String(score), WIDTH - 60, y + 16);
      ctx.textAlign = "left";
      const barX = 190;
      const barW = WIDTH - 60 - 50 - barX;
      ctx.strokeStyle = main;
      ctx.lineWidth = 2;
      ctx.strokeRect(barX, y + 2, barW, 16);
      ctx.fillStyle = black ? "#e0301e" : "#f9dc33";
      ctx.fillRect(barX + 2, y + 4, ((score + 30) / 60) * (barW - 4), 12);
      y += 34;
    });

    if (d.item) {
      ctx.textAlign = "center";
      ctx.fillStyle = text;
      ctx.font = "bold 18px sans-serif";
      ctx.fillText("おすすめのアイテム：" + d.item, WIDTH / 2, Math.max(y + 30, 700));
    }

    const link = document.createElement("a");
    link.download = "frenemy-" + d.role + ".png";
    link.href = canvas.toDataURL("image/png");
    link.click();
  };

  button.addEventListener("click", () => {
    draw().catch(() => alert("画像を作れませんでした。"));
  });
});
