package model;

import java.util.ArrayList;

public class Book {
	private String bookID,name;
	private int chapNumber;
	private ArrayList<Chap> chapList = new ArrayList<>(chapNumber);
	public Book(String bookID, String name, int chapNumber, ArrayList<Chap> chapList) {
		this.bookID = bookID;
		this.name = name;
		this.chapNumber = chapNumber;
		this.chapList = chapList;
	}
	public Book() {}
	public String getBookID() {
		return bookID;
	}
	public void setBookID(String bookID) {
		this.bookID = bookID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getChapNumber() {
		return chapNumber;
	}
	public void setChapNumber(int chapNumber) {
		this.chapNumber = chapNumber;
	}
	public ArrayList<Chap> getChapList() {
		return chapList;
	}
	public void setChapList(ArrayList<Chap> chapList) {
		this.chapList = chapList;
	}
}
