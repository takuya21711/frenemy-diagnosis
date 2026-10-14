// アカウントアイコンをクリックしたらメニューを開閉する
document.addEventListener("DOMContentLoaded", () => {
	/*
	button変数：
	menu変数：
	logoutButton変数: 
	*/
  const button = document.getElementById("accountButton");
  const menu = document.getElementById("accountMenu");
  const logoutButton = document.getElementById("logoutButton");

  if (!button || !menu) {
    return;
  }

  const closeMenu = () => {
    menu.hidden = true;
    button.setAttribute("aria-expanded", "false");
  };

  button.addEventListener("click", (event) => {
    event.stopPropagation();
    const willOpen = menu.hidden;
    menu.hidden = !willOpen;
    button.setAttribute("aria-expanded", String(willOpen));
  });

  // メニューの外をクリック or Escキーで閉じる
  document.addEventListener("click", (event) => {
    if (!menu.contains(event.target)) {
      closeMenu();
    }
  });
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeMenu();
    }
  });

  // TODO: ログアウト機能ができたら削除する（今はモック）
  logoutButton.addEventListener("click", () => {
    alert("ログアウトしました（モック）");
    closeMenu();
  });
});
