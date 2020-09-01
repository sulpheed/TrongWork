package model;

public class Result {
	private String testcode;
	private String userNumber;
	private java.sql.Date day;
	private String result;
	private int getExp;
	public String getTestcode() {
		return testcode;
	}
	public void setTestcode(String testcode) {
		this.testcode = testcode;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	public java.sql.Date getDay() {
		return day;
	}
	public void setDay(java.sql.Date day) {
		this.day = day;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getGetExp() {
		return getExp;
	}
	public void setGetExp(int getExp) {
		this.getExp = getExp;
	}
}
