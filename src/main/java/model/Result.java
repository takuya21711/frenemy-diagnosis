package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 診断結果（resultテーブル ＋ 役職・画像・アイテム）
 */
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private int resultId;
	private Role role;
	/** 5特性のスコア。クリティカル吉嵜のときは空 */
	private List<TraitScore> traits = new ArrayList<>();
	private String imagePath;
	private String itemName;
	private Date createdAt;

	public Result() {}
	public Result(int resultId, Role role, String imagePath, String itemName, Date createdAt) {
		this.resultId = resultId;
		this.role = role;
		this.imagePath = imagePath;
		this.itemName = itemName;
		this.createdAt = createdAt;
	}
	public int getResultId() {return resultId;}
	public Role getRole() {return role;}
	public List<TraitScore> getTraits() {return traits;}
	public String getImagePath() {return imagePath;}
	public String getItemName() {return itemName;}
	public Date getCreatedAt() {return createdAt;}
}
