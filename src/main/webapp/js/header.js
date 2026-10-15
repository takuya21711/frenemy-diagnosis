// ヘッダーのメニュー開閉
// ・アカウントアイコン → 名前とログアウトのメニュー
// ・ハンバーガーボタン（スマホのみ） → 画面遷移用のメニュー
document.addEventListener("DOMContentLoaded", () => {
	/*
	button変数：開閉ボタン
	menu変数：開閉するメニュー
	*/
  const setupToggle = (button, menu, isOpen, setOpen) => {
    if (!button || !menu) {
      return;
    }

    const closeMenu = () => {
      setOpen(false);
      button.setAttribute("aria-expanded", "false");
    };

    button.addEventListener("click", (event) => {
      event.stopPropagation(); //親要素にeventが伝わると、勝手に知らない機能が実行されるため制御するメソッド
      const willOpen = !isOpen();
      setOpen(willOpen);
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
  };

  // アカウントメニュー（hidden属性で開閉）
  const accountMenu = document.getElementById("accountMenu");
  setupToggle(
    document.getElementById("accountButton"),
    accountMenu,
    () => !accountMenu.hidden,
    (open) => { accountMenu.hidden = !open; }
  );

  // ハンバーガーメニュー（md以上では CSS で常に表示されるので、hidden クラスで開閉）
  const nav = document.getElementById("globalNav");
  setupToggle(
    document.getElementById("menuButton"),
    nav,
    () => !nav.classList.contains("hidden"),
    (open) => { nav.classList.toggle("hidden", !open); }
  );
});
