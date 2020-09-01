package model;

public class Chap {
	private String chapID,chapName;

	public Chap(String chapID, String chapName) {
		this.chapID = chapID;
		this.chapName = chapName;
	}
	public Chap() {}
	public String getChapID() {
		return chapID;
	}

	public void setChapID(String chapID) {
		this.chapID = chapID;
	}

	public String getChapName() {
		return chapName;
	}

	public void setChapName(String chapName) {
		this.chapName = chapName;
	}
}
