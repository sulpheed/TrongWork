package model;

public class Test {
	private String testCode;
	private java.sql.Date startDay;
	private java.sql.Date endDay;
	private String type;
	private String classCode;
	private String skill;
	private String book;
	private String lesson;
	private int status;
	public String getTestCode() {
		return testCode;
	}
	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}
	public String getType() {
		return type;
	}
	public java.sql.Date getStartDay() {
		return startDay;
	}
	public void setStartDay(java.sql.Date startDay) {
		this.startDay = startDay;
	}
	public java.sql.Date getEndDay() {
		return endDay;
	}
	public void setEndDay(java.sql.Date endDay) {
		this.endDay = endDay;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getLesson() {
		return lesson;
	}
	public void setLesson(String lesson) {
		this.lesson = lesson;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
