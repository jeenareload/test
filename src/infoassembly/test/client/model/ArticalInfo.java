package infoassembly.test.client.model;

public class ArticalInfo {

	private int articalID;
	private String text;

	public ArticalInfo(String text, int articalID) {
		this.text=text;
		this.articalID=articalID;
	}

	public String getText() {
		return text;
	}

	public int getArticalID() {
		return articalID;
	}

}
