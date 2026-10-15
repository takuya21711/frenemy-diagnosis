package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 役職（roleテーブルの1行 ＋ 閾値）
 */
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	/** クリティカル吉嵜の役職ID（閾値なし。クリティカルでだけ出る） */
	public static final int YOSHIZAKI_ID = 15;

	private int id;
	private String name;
	private String description;
	/** TRUEなら黒の役職（結果画面が「お前は」＋怖い背景になる） */
	private boolean black;
	private List<Threshold> thresholds = new ArrayList<>();
	/** 役職一覧で使う画像（webappからの相対パス） */
	private String imagePath;

	public Role() {}
	public Role(int id, String name, String description, boolean black) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.black = black;
	}
	public int getId() {return id;}
	public String getName() {return name;}
	public String getDescription() {return description;}
	public boolean isBlack() {return black;}
	public List<Threshold> getThresholds() {return thresholds;}
	public String getImagePath() {return imagePath;}
	public void setImagePath(String imagePath) {this.imagePath = imagePath;}
	public boolean isYoshizaki() {return id == YOSHIZAKI_ID;}
}
