package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;
import model.Book;
import model.Quiz;
import model.Student;
import model.Test;

public class GetTest extends Action{
	public static ArrayList<Book> bookList = null;
	public static Book newBook = null;
	public static ArrayList<Quiz> quizAry = null;
	public static ArrayList<Quiz> testDemo = null;
	public static ArrayList<Test> history = null;
	public static Test testSample = null;
	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		DBConnection db = new DBConnection();
		String code = req.getParameter("code");
		String book = req.getParameter("book");
		int bookCnt = -1,chapCnt = 0;
		if(book != null && !"none".equals(book)) bookCnt = Integer.parseInt(book);
		String chap = req.getParameter("chap");
		if(chap != null && !"none".equals(chap)) chapCnt = Integer.parseInt(chap);
		HttpSession sess = req.getSession();
		String stay,next;
		switch(code) {
			case "history":{
				next = "/jsp/testHistory.jsp";
				history = null;
				if(history == null) history = db.getTest();
				if(history == null) {
					req.setAttribute("history", history);
					req.getRequestDispatcher("/jsp/historyNull.jsp").forward(req, res);
				}else {
					req.setAttribute("history", history);
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "viewTest":{
				String id = req.getParameter("testID");
				String status = req.getParameter("st");
				testDemo = db.getTestQuiz(id);
				testSample = db.getOneTest(id);
				if(status.equals("nonedit")) {
					next = "/jsp/nonEditDemo.jsp";
				}else {
					next = "/jsp/editContent.jsp";
				}
				req.setAttribute("testSample", testSample);
				req.setAttribute("testDemo", testDemo);
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "choseType":{
				next = "/jsp/choseType.jsp";
				testSample = null;
				testDemo = null;
				if(testSample == null) testSample = new Test();
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "getContent":{
				String type = req.getParameter("type");
				if(testSample == null) testSample = new Test();
				if(testSample.getType() == null) testSample.setType(type);
				String jsp = "/jsp/getContent.jsp";
				try {
					String content = req.getParameter("content");
					String skill = req.getParameter("skill");
					if(bookList == null) bookList = db.getBook();
					req.setAttribute("skill", skill);
					req.setAttribute("content", content);
					if(bookCnt != -1) {
						req.setAttribute("bookSelected", bookList.get(bookCnt));
					}else {
						Book unknown = new Book();
						unknown.setName("none");
						req.setAttribute("bookSelected",unknown);
					}
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(jsp).forward(req, res);
				}catch(Exception e) {
					jsp = "/jsp/noticePage.jsp";
					stay = "/jsp/" + sess.getAttribute("role") + ".jsp";
					req.setAttribute("target",stay);
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "makeDemo":{
				String group = req.getParameter("group");
				stay = "/jsp/getContent.jsp";
				next = "/jsp/demo.jsp";
				Quiz qTemp = new Quiz();
				if("none".equals(req.getParameter("skill"))) {
					qTemp.setsCode("");
				}else {
					qTemp.setsCode(req.getParameter("skill"));
				}
				qTemp.setContent("");
				if(bookCnt != -1) {
					qTemp.setbCode(bookList.get(bookCnt).getName());
				}else {
					qTemp.setbCode("");
				}
				if("none".equals(req.getParameter("chap"))) {
					qTemp.setlCode("");
				}else {
					qTemp.setlCode(bookList.get(bookCnt).getChapList().get(chapCnt).getChapID());
				}
				quizAry = null;
				if(quizAry == null) quizAry = db.searchQuiz(qTemp);
				if(quizAry == null) {
					req.setAttribute("notice", "条件にあう問題がない。");
					if(bookCnt == -1) bookCnt = 0;
					req.setAttribute("bookSelected", bookList.get(bookCnt));
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					if("none".equals(req.getParameter("skill"))) {
						sess.setAttribute("skillSet","N/A");
						testSample.setSkill("");
					}else {
						sess.setAttribute("skillSet", req.getParameter("skill"));
						testSample.setSkill(req.getParameter("skill"));
					}
					if(bookCnt != -1) {
						sess.setAttribute("bookSelected", bookList.get(bookCnt).getName());
						testSample.setBook(bookList.get(bookCnt).getName());
					}else {
						sess.setAttribute("bookSelected", "N/A");
						testSample.setBook("");
					}
					if(!"none".equals(chap)) {
						sess.setAttribute("chapSelected", bookList.get(bookCnt).getChapList().get(chapCnt).getChapName());
						testSample.setLesson(bookList.get(bookCnt).getChapList().get(chapCnt).getChapName());
					}else {
						sess.setAttribute("chapSelected", "N/A");
						testSample.setLesson("");
					}
					req.setAttribute("group", group);
					testSample.setClassCode(group);
					System.out.println(testSample.getType());
					if("A".equals(testSample.getType())) {
						req.setAttribute("type","小テスト" );
						req.setAttribute("quantity", "10問");
					}else {
						req.setAttribute("type","模擬試験" );
						req.setAttribute("quantity", "30問");
					}
					testDemo = null;
					if(testDemo == null) {
						testDemo = createTestContent(quizAry,"A".equals(testSample.getType())? 10:30);
					}
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "editDemo":{
				next = "/jsp/editDemo.jsp";
				req.setAttribute("testSample", testSample);
				req.setAttribute("testDemo", testDemo);
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "delete":{
				stay = "/jsp/editDemo.jsp";
				String id = req.getParameter("ID");
				for(Quiz q:testDemo) {
					if(id.equals(q.getqCode())) {
						if(testDemo.size() > 1) {
							testDemo.remove(q);
							req.setAttribute("testSample", testSample);
							req.setAttribute("testDemo", testDemo);
							req.setAttribute("notice", "1件が削除された");
							req.getRequestDispatcher(stay).forward(req, res);
						}else {
							req.setAttribute("testSample", testSample);
							req.setAttribute("testDemo", testDemo);
							req.setAttribute("notice", "これ以上削除できない。");
							req.getRequestDispatcher(stay).forward(req, res);
						}
					}
				}

			}
			break;
			case "searchToAdd":{
				stay = "/jsp/searchToAddMenu.jsp";
				try {
					String content = req.getParameter("content");
					String skill = req.getParameter("skill");
					if(bookList == null) bookList = db.getBook();
					req.setAttribute("skill", skill);
					req.setAttribute("content", content);
					if(bookCnt != -1) {
						req.setAttribute("bookSelected", bookList.get(bookCnt));
					}else {
						Book unknown = new Book();
						unknown.setName("none");
						req.setAttribute("bookSelected",unknown);
					}
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(stay).forward(req, res);
				}catch(Exception e) {
					stay = "/jsp/" + sess.getAttribute("role") + ".jsp";
					req.setAttribute("target",stay);
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "searchToAddResults":{
				stay = "/jsp/searchToAddMenu.jsp";
				next = "/jsp/searchToAddResults.jsp";
				Quiz sample = new Quiz();
				if("none".equals(req.getParameter("skill"))) {
					sample.setsCode("");
				}else {
					sample.setsCode(req.getParameter("skill"));
				}
				sample.setContent(req.getParameter("content"));
				if(bookCnt != -1) {
					sample.setbCode(bookList.get(bookCnt).getName());
				}else {
					sample.setbCode("");
				}
				if("none".equals(req.getParameter("chap"))) {
					sample.setlCode("");
				}else {
					sample.setlCode(bookList.get(bookCnt).getChapList().get(chapCnt).getChapID());
				}
				quizAry = null;
				if(quizAry == null) quizAry = db.searchQuiz(sample);
				if(quizAry == null) {
					req.setAttribute("notice", "条件に合う問題がない");
					if(bookCnt == -1) bookCnt = 0;
					req.setAttribute("bookSelected", bookList.get(bookCnt));
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					if("none".equals(req.getParameter("skill"))) {
						sess.setAttribute("skillSet","N/A");
					}else {
						sess.setAttribute("skillSet", req.getParameter("skill"));
					}
					if(bookCnt != -1) {
						sess.setAttribute("bookSelected", bookList.get(bookCnt).getName());
					}else {
						sess.setAttribute("bookSelected", "N/A");
					}
					if("".equals(sample.getContent())) {
						sess.setAttribute("keyword", "入力なし");
					}else {
						sess.setAttribute("keyword", sample.getContent());
					}
					if(!"none".equals(chap)) {
						sess.setAttribute("chapSelected", bookList.get(bookCnt).getChapList().get(chapCnt).getChapID());
					}else {
						sess.setAttribute("chapSelected", "N/A");
					}
					req.setAttribute("quizAry",quizAry);
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "addToTest":{
				stay = "/jsp/searchToAddResults.jsp";
				String id = req.getParameter("ID");
				for(Quiz q:quizAry) {
					if(id.equals(q.getqCode())) {
						if(testDemo.contains(q)) {
							req.setAttribute("quizAry",quizAry);
							req.setAttribute("notice", "é‡�è¤‡ã�•ã‚Œã�¾ã�—ã�Ÿã€‚");
							req.getRequestDispatcher(stay).forward(req, res);
						}else {
							testDemo.add(q);
							req.setAttribute("quizAry",quizAry);
							req.setAttribute("notice", "1件が追加された");
							req.getRequestDispatcher(stay).forward(req, res);
						}
					}
				}
			}
			break;
			case "saveTest":{
				System.out.println("WTH");
				next = "/jsp/" + sess.getAttribute("role").toString() + ".jsp";
				boolean check = false;
				System.out.println(sess.getAttribute("role").toString());
				System.out.println(next);
				System.out.println(testSample.getTestCode());
				for(Test t :history) {
					if(testSample.getTestCode()!=null&&testSample.getTestCode().equals(t.getTestCode())) {
						System.out.println(t.getTestCode());
						check = true;
						break;
					}
				}
				System.out.println(check);
				if(!check) {
					int testCode = db.getTestNumber(testSample.getType()) + 1;
					if(testSample.getTestCode() == null) {
					testSample.setTestCode(testSample.getType() + testCode);
					}
					testSample.setStatus(0);
					db.saveTest(testSample, testDemo);
					testSample = null;
					testDemo = null;
					quizAry = null;
					history = null;
					testSample = null;
					req.setAttribute("notice", "テストの1件が保存された");
					req.getRequestDispatcher(next).forward(req, res);
				}else {
					db.updateTest(testSample, testDemo);
					req.setAttribute("notice", "テストが更新された。");
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "setTime":{
				String status = req.getParameter("st");
				stay = "/jsp/" + status + ".jsp";
				next = "/jsp/setTime.jsp";
				int maxNumber = "A".equals(testSample.getType()) ? 10:30;
				if(testDemo.size() == maxNumber) {
//					ArrayList<Student> group = db.searchByClass(testSample.getClassCode());
//					int testCode = db.getTestNumber(testSample.getType()) + 1;
//					if(testSample.getTestCode() == null) {
//						testSample.setTestCode(testSample.getType() + testCode);
//					}
//					db.sendTest(testSample.getTestCode(), group);
					req.setAttribute("testSample", testSample);
					req.getRequestDispatcher(next).forward(req, res);
				}else {
					req.setAttribute("testSample", testSample);
					req.setAttribute("testDemo", testDemo);
					req.setAttribute("notice", "現在の問題数と基本数は違い");
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "sendTest":{
				stay = "/jsp/setTime.jsp";
				next = "/jsp/" + sess.getAttribute("role").toString() + ".jsp";
				String y = req.getParameter("year");
				String m = req.getParameter("month");
				String d = req.getParameter("day");
				if(checkDigit(y)&&checkDigit(m)&&checkDigit(d)&&!"".equals(y)&&!"".equals(m)&&!"".equals(d)) {
					int year = Integer.parseInt(y);
					int month = Integer.parseInt(m) - 1;
					int day = Integer.parseInt(d);
					Calendar c = Calendar.getInstance();
					c.set(Calendar.MONTH, month);
			        c.set(Calendar.DATE, day);
			        c.set(Calendar.YEAR, year);
			        Date shimekiri = new Date();
			        java.sql.Date start = new java.sql.Date(shimekiri.getTime());
			        shimekiri = c.getTime();
			        java.sql.Date end = new java.sql.Date(shimekiri.getTime());
			        testSample.setStartDay(start);
			        testSample.setEndDay(end);
					ArrayList<Student> group = db.searchByClass(testSample.getClassCode());
					int testCode = db.getTestNumber(testSample.getType()) + 1;
					testSample.setStatus(1);
					if(testSample.getTestCode() == null) {
						testSample.setTestCode(testSample.getType() + testCode);
						db.saveTestDetails(testSample,testDemo);
					}else {
						System.out.println(testSample.getStartDay());
						System.out.println(testSample.getEndDay());
						System.out.println(testSample.getTestCode());
						db.updateTestDetails(testSample,testDemo);
					}

					db.sendTest(testSample, group);
					req.setAttribute("notice", "送信しました。");
					req.getRequestDispatcher(next).forward(req, res);
				}else {
					req.setAttribute("notice", "接続エラー");
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
		}
		return "/jsp/login.jsp";
	}
	public ArrayList<Quiz> createTestContent(ArrayList<Quiz> tmp,int num) {
		Random rd = new Random();
		ArrayList<Quiz> atarashi = null;
		if(tmp.size() < num) {
			atarashi = new ArrayList<Quiz>(tmp);
		}else {
			if(atarashi == null) atarashi = new ArrayList<Quiz>();
			for(int a = 0;a < num;a++) {
				int i = rd.nextInt(tmp.size());
				atarashi.add(tmp.get(i));
				tmp.remove(i);
			}
		}
		return atarashi;
	}
	public boolean checkDigit(String str) {
		boolean c = true;
		for(int i = 0;i < str.length();i++) {
			if(!Character.isDigit(str.charAt(i))) {
				c = false;
			}
		}
		return c;
	}

}
