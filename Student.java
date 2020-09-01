package model;

public class Student extends User{
	private String certificate;
	private String className;
	private int level,reading,grammar,vocabulary;
	private String link ;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getReading() {
		return reading;
	}
	public void setReading(int reading) {
		this.reading = reading;
	}
	public int getGrammar() {
		return grammar;
	}
	public void setGrammar(int grammar) {
		this.grammar = grammar;
	}
	public int getVocabulary() {
		return vocabulary;
	}
	public void setVocabulary(int vocabulary) {
		this.vocabulary = vocabulary;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
