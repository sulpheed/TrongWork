package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;
import model.Book;
import model.Quiz;
import model.Result;
import model.Test;

public class doTest extends Action{
	public static ArrayList<Book> bookList = null;
	public static Book newBook = null;
	public static ArrayList<Quiz> quizAry = null;
	public static ArrayList<Quiz> testForm = null;
	public static ArrayList<String> rightChoice = null;
	public static ArrayList<String[]> makeChoice = null;
	public static ArrayList<Test> nowTest = null;
	public static Test testSample = null;
	public static ArrayList<Result> rsHistory = null;
	public static Result testRs = null;

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
			case "testList":{
				stay = "/jsp/student.jsp";
				next = "/jsp/testList.jsp";
				rsHistory = null;
				if(rsHistory == null) rsHistory = db.getTestList(sess.getAttribute("ID").toString());
				if(rsHistory != null) {
					if(nowTest == null) nowTest = new ArrayList<Test>();
					for(Result rs:rsHistory) {
						Test t = new Test();
						t = db.getOneTest(rs.getTestcode());
						nowTest.add(t);
					}
					req.setAttribute("nowTest", nowTest);
					nowTest = null;
					req.getRequestDispatcher("/jsp/testList.jsp").forward(req, res);
				}else {
					req.setAttribute("notice", "現在はテストがないのでお待ちください。");
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "choseTest":{
				String id = req.getParameter("testID");
				quizAry = null;
				testSample = null;
				if(testSample == null) testSample = db.getOneTest(id);
				if(quizAry == null) quizAry = db.getTestQuiz(id);
				next = "/jsp/warning.jsp";
				req.setAttribute("notice", "テストを始まる上で最後までにもどれないでよろしいですか。");
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "startTest":{
				next = "/jsp/doTest.jsp";
				rightChoice = getAns(quizAry);
				if(testForm == null) testForm = shuffle(quizAry);
				Date date = new Date();
				if(testRs == null) {
					testRs = new Result();
					testRs.setUserNumber(sess.getAttribute("ID").toString());
					testRs.setDay(new java.sql.Date(date.getTime()));
					testRs.setResult("0/" + ("A".equals(testSample.getType()) ? 10:30));
					testRs.setGetExp(0);
					testRs.setTestcode(testSample.getTestCode());
				}
				db.updateRs(testRs);
				req.setAttribute("selected", "");
				req.setAttribute("testSample", testSample);
				req.setAttribute("quest", testForm.get(0));
				req.setAttribute("number", new Integer(0));
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "doTest":{
				stay = "/jsp/doTest.jsp";
				int number = 0;
				int newQuest = 0;
				int maxNum = "A".equals(testSample.getType())? 10:30;
				if(req.getParameter("number") == null) {
					number = 0;
				}else {
					number = Integer.parseInt(req.getParameter("number"));
				}
				System.out.println("number = " + number);
				String step = req.getParameter("step");
				System.out.println("step = " + step);
				if("back".equals(step)) {
					newQuest = number - 1;
				}else if("next".equals(step)){
					newQuest = number + 1;
				}
				String choice = req.getParameter("choice");
				System.out.println("choice =" +choice);

				if(makeChoice == null) {
					makeChoice = new ArrayList<String[]>(testForm.size());

					for(int i = 0;i < maxNum;i++) {
						String[] selected = new String[2];
						selected[0] = i+"";
						selected[1] = "";
						makeChoice.add(selected);
					}
				}

				if(choice != null) {

					//System.out.println(makeChoice.size());
					for(String[] ss : makeChoice) {

						if(req.getParameter("number").equals(ss[0])) {
							System.out.println(ss[0]);
							System.out.println(req.getParameter("number"));
							ss[1] = choice;
						}
					}
				}

//				System.out.println("newQuest2 = "+ newQuest);
//				System.out.println("selected ="  + makeChoice.get(newQuest)[1]);
				req.setAttribute("selected", makeChoice.get(newQuest)[1]);
				req.setAttribute("testSample", testSample);
				req.setAttribute("quest",testForm.get(newQuest));
				req.setAttribute("number", new Integer(newQuest));
				req.getRequestDispatcher(stay).forward(req, res);
			}
			break;
			case "endTest":{
				next = "/jsp/ResultSc.jsp";
				int kanj=0,gram=0,read=0;
				ArrayList<Quiz> getRight = new ArrayList<Quiz>();
				for(int i = 0;i < testForm.size();i++) {
					String ans = selected(testForm.get(i),Integer.parseInt(makeChoice.get(i)[1]));
					if(rightChoice.get(i).equals(ans)) {
						int point = getExp(testForm.get(i));
						if("KANJI".equals(testForm.get(i).getsCode())) {
							kanj += point;
						}else if("GRAMMAR".equals(testForm.get(i).getsCode())) {
							gram += point;
						}else if("READING".equals(testForm.get(i).getsCode())) {
							read += point;
						}
						getRight.add(testForm.get(i));
					}
				}
				
				req.setAttribute("testSample", testSample);
				req.setAttribute("testForm",testForm);
				req.getRequestDispatcher(next).forward(req, res);
			}
		}
		return "/jsp/login.jsp";
	}
	public ArrayList<String> getAns(ArrayList<Quiz> ar) {
		ArrayList<String> right = new ArrayList<String>();
		for(Quiz q:ar) {
			right.add(q.getAns1());
		}
		return right;
	}
	public ArrayList<String> getChoice(Quiz ar){
		ArrayList<String> choice = new ArrayList<String>();
		choice.add(ar.getAns1());
		choice.add(ar.getAns2());
		choice.add(ar.getAns3());
		choice.add(ar.getAns4());
		return choice;
	}
	public ArrayList<Quiz> shuffle(ArrayList<Quiz> ar){
		ArrayList<Quiz> test = new ArrayList<Quiz>();
		ArrayList<String> choice = null;
		Random rd = new Random();
		for(Quiz q : ar) {
			choice = getChoice(q);
			for(String s:choice) {
				int oldPosition = choice.indexOf(s);
				String temp = s;
				int newPosition = rd.nextInt(4);
				if(newPosition != oldPosition) {
					choice.set(oldPosition, choice.get(newPosition));
					choice.set(newPosition, temp);
				}
			}
			q.setAns1(choice.get(0));
			q.setAns2(choice.get(1));
			q.setAns3(choice.get(2));
			q.setAns4(choice.get(3));
			test.add(q);
		}
		return test;
	}
	public int getPoint(Quiz q) {
		int point = 0;
		if("KANJI".equals(q.getsCode())) {
			point = 3;
		}
		if("GRAMMAR".equals(q.getsCode())) {
			point = 6;
		}
		if("READING".equals(q.getsCode())) {
			point = 10;
		}
		return point;
	}
	public String selected(Quiz q,int choice) {
		String select = "";
		if(choice == 0) {
			select = q.getAns1();
		}else if(choice == 1) {
			select = q.getAns2();
		}else if(choice == 2) {
			select = q.getAns3();
		}else if(choice == 3) {
			select = q.getAns4();
		}
		return select;
	}
	public int getExp(Quiz q) {
		int exp = 0;
		if("KANJI".equals(q.getsCode())) {
			exp = exp + 3;
		}else if("GRAMMAR".equals(q.getsCode())) {
			exp = exp + 5;
		}else if("READING".equals(q.getsCode())) {
			exp = exp + 10;
		}
		return exp;
	}
}
