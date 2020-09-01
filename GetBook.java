package control;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;
import dao.csvReader;
import model.Book;
import model.Chap;
import model.Quiz;

public class GetBook extends Action{
	public static ArrayList<Book> bookList = null;
	public static Book newBook = null;
	public static ArrayList<Quiz> quizAry = null;
	public static Quiz newQuiz = null;
	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		// TODO è‡ªå‹•ç”Ÿæˆ�ã�•ã‚Œã�Ÿãƒ¡ã‚½ãƒƒãƒ‰ãƒ»ã‚¹ã‚¿ãƒ–
		res.setContentType("UTF-8");
		res.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		String jsp = "/jsp/openABook.jsp";
		String code = req.getParameter("code");
		String book = req.getParameter("book");
		int bookCnt = -1,chapCnt = 0;
		if(book != null && !"none".equals(book)) bookCnt = Integer.parseInt(book);
		String chap = req.getParameter("chap");
		if(chap != null && !"none".equals(chap)) chapCnt = Integer.parseInt(chap);
		HttpSession sess = req.getSession();
		String stay,next;
		DBConnection db = new DBConnection();
		switch(code) {
			case "menu":{
				try {
					if(bookList == null) bookList = db.getBook();
					if(bookCnt == -1) bookCnt = 0;
					req.setAttribute("bookSelected", bookList.get(bookCnt));
					req.setAttribute("bookList", bookList);
					req.setAttribute("target",jsp );
					req.getRequestDispatcher(jsp).forward(req, res);
				}catch(Exception e) {
					jsp = "/jsp/noticePage.jsp";
					stay = "/jsp/" + sess.getAttribute("role") + ".jsp";
					req.setAttribute("target",stay);
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "add":{

				stay = "/jsp/addBook.jsp";
				next = "/jsp/addBook2.jsp";
				int n = 0;
				String bookName = req.getParameter("bookName");
				String chapNumber = req.getParameter("chapNumber");
				if("".equals(bookName)||"".equals(chapNumber)) {
					req.setAttribute("notice", "未入力項目があります。");
					req.getRequestDispatcher(stay).forward(req, res);
				}
				for(int i = 0;i < chapNumber.length();i++) {
					if(!Character.isDigit(chapNumber.charAt(i))) {
						req.setAttribute("notice", "数字のみを入力してください。");
						req.getRequestDispatcher(stay).forward(req, res);
					}
				}
				n = Integer.parseInt(chapNumber);
				if(newBook == null) newBook = new Book();
				newBook.setName(bookName);
				newBook.setChapNumber(n);
				req.setAttribute("bookName", newBook.getName());
				req.setAttribute("chapNumber",newBook.getChapNumber());
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "add2":{
				next = "/jsp/addChap.jsp";
				req.setAttribute("bookName", newBook.getName());
				req.setAttribute("chapNumber",newBook.getChapNumber());
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "addChap":{
				stay = "/jsp/addChap.jsp";
				next = "/jsp/newBookView.jsp";
				Chap c = null;
				Boolean check = false;
				String[] chapArr = new String[newBook.getChapNumber()];
				for(int i = 0;i < newBook.getChapNumber();i++) {
					chapArr[i] = req.getParameter("chap" + (i + 1));
					if("".equals(req.getParameter("chap" + (i + 1)))){
						check = true;
					}
				}
				if(check) {
					for(int i = 0;i < newBook.getChapNumber();i++) {
						req.setAttribute("chapName"+(i+1), chapArr[i]);
					}
					req.setAttribute("bookName", newBook.getName());
					req.setAttribute("chapNumber", newBook.getChapNumber());
					req.setAttribute("notice", "未入力章の名がある。");
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					for(int i = 0;i < newBook.getChapNumber();i++) {
						c = new Chap("章"+(i+1),chapArr[i]);
						req.setAttribute("chap"+(i+1),c.getChapID());
						req.setAttribute("chapName"+(i+1),c.getChapName());
						newBook.getChapList().add(c);
					}
					req.setAttribute("bookName", newBook.getName());
					req.setAttribute("chapNumber", newBook.getChapNumber());
					req.setAttribute("notice", "上記の教科書を登録してよろしいでしょうか。");
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "saveBook":{
				stay = "/jsp/newBookView.jsp";
				next = "/jsp/openABook.jsp";
				try {
					db.saveBook(newBook);
					bookList = null;
					newBook = null;
					req.setAttribute("notice", "新しい教科書が追加されました。");
					if(bookList == null) bookList = db.getBook();
					if(bookCnt == -1) bookCnt = 0;
					req.setAttribute("bookSelected", bookList.get(bookCnt));
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(next).forward(req, res);
				}catch(Exception e) {
					req.setAttribute("notice", "接続エラー");
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "addQuiz":{
				//stay = "/jsp/openABook.jsp";
				next = "/jsp/addQuiz.jsp";
				req.setAttribute("bookSelected", bookList.get(bookCnt));
				req.setAttribute("chapSelected", bookList.get(bookCnt).getChapList().get(chapCnt));
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "readFile":{
				stay = "/jsp/addQuiz.jsp";
				next = "/jsp/newQReview.jsp";
				if("".equals(req.getParameter("avatar"))) {
					res.sendRedirect("../jsp/addQuiz.jsp");
				}else {
					try {
						int quizCode = db.getCount("question");
						csvReader rd = new csvReader();
						ArrayList<String> tmp = rd.readFile(req.getParameter("avatar"));
						String bookID = bookList.get(bookCnt).getName();
						String chapID = bookList.get(bookCnt).getChapList().get(chapCnt).getChapID();
						quizAry = convert(bookID,chapID,tmp,quizCode);
						req.setAttribute("notice", "それらの問題を追加しますか。");
						req.setAttribute("quizAry", quizAry);
						req.setAttribute("chapName", bookList.get(bookCnt).getChapList().get(chapCnt).getChapName());
						req.getRequestDispatcher(next).forward(req, res);
					}catch(Exception e) {
						req.setAttribute("notice", "読み込みエラー");
						req.getRequestDispatcher(stay).forward(req, res);
					}
				}
			}
			break;

			case "saveQuest":{
				stay = "/jsp/newQReview.jsp";
				next = "../html/uw13 addQSuccess.html";
				jsp = "/jsp/noticePage.jsp";
				try {
					int cnt = 0;
					for(Quiz q : quizAry) {
						db.saveQuiz(q);
						cnt++;
					}
					req.setAttribute("notice", cnt + "問が追加された。");
					req.setAttribute("target", next);
					quizAry = null;
					req.getRequestDispatcher(jsp).forward(req, res);
				}catch(Exception e) {
					req.setAttribute("notice", "接続エラー");
					req.getRequestDispatcher(stay).forward(req, res);
				}
			}
			break;
			case "readOne":{
				Quiz q = null;
				stay = "/jsp/addQuiz.jsp";
				next = "/jsp/newQReview.jsp";
				String skill = req.getParameter("skill");
				String content = req.getParameter("content");
				String choice1 = req.getParameter("choice1");
				String choice2 = req.getParameter("choice2");
				String choice3 = req.getParameter("choice3");
				String choice4 = req.getParameter("choice4");
				if("".equals(content)||"".equals(choice1)||"".equals(choice2)||"".equals(choice3)||"".equals(choice4)) {
					req.setAttribute("notice", "未入力項目がある");
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					q = new Quiz();
					q.setsCode(skill);
					q.setqCode(db.getCount("question") + 1 + "");
					q.setlCode(bookList.get(bookCnt).getChapList().get(chapCnt).getChapID());
					q.setContent(content);
					q.setbCode(bookList.get(bookCnt).getName());
					q.setAns1(choice1);
					q.setAns2(choice2);
					q.setAns3(choice3);
					q.setAns4(choice4);
					if(quizAry == null || quizAry.size() < 2) {
						quizAry = new ArrayList<Quiz>();
						quizAry.add(q);
					}
					req.setAttribute("notice", "下の問題を登録してよろしいですか。");
					req.setAttribute("quizAry", quizAry);
					req.setAttribute("chapName", bookList.get(bookCnt).getChapList().get(chapCnt).getChapName());
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "searchQMenu":{
				jsp = "/jsp/searchQMenu.jsp";
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
			case "search":{
				stay = "/jsp/searchQMenu.jsp";
				next = "/jsp/searchQResults.jsp";
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
					req.setAttribute("notice", "条件に合う問題がありません。");
					if(bookCnt == -1) bookCnt = 0;
					req.setAttribute("bookSelected", bookList.get(bookCnt));
					req.setAttribute("bookList", bookList);
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					if("none".equals(req.getParameter("skill"))) {
						req.setAttribute("skillSet","N/A");
					}else {
						req.setAttribute("skillSet", req.getParameter("skill"));
					}
					if(bookCnt != -1) {
						req.setAttribute("bookSelected", bookList.get(bookCnt).getName());
					}else {
						req.setAttribute("bookSelected", "N/A");
					}
					if("".equals(sample.getContent())) {
						req.setAttribute("keyword", "å…¥åŠ›ç„¡ã�—");
					}else {
						req.setAttribute("keyword", sample.getContent());
					}
					if(!"none".equals(chap)) {
						req.setAttribute("chapSelected", bookList.get(bookCnt).getChapList().get(chapCnt).getChapID());
					}else {
						req.setAttribute("chapSelected", "N/A");
					}
					req.setAttribute("quizAry",quizAry);
					req.getRequestDispatcher(next).forward(req, res);
				}
			}
			break;
			case "change":{
				next = "/jsp/quizChange.jsp";
				String id = req.getParameter("quizID");
				for(Quiz q:quizAry) {
					if(id.equals(q.getqCode())) newQuiz = q;
				}
				req.setAttribute("quiz", newQuiz);
				req.getRequestDispatcher(next).forward(req, res);
			}
			break;
			case "newQuizView":{
				stay = "/jsp/quizChange.jsp";
				next = "/jsp/newQuizView.jsp";
				String content = req.getParameter("content");
				String choice1 = req.getParameter("choice1");
				String choice2 = req.getParameter("choice2");
				String choice3 = req.getParameter("choice3");
				String choice4 = req.getParameter("choice4");
				if("".equals(content)||"".equals(choice1)||"".equals(choice2)||"".equals(choice3)||"".equals(choice4)) {
					req.setAttribute("notice", "未入力項目がある");
					req.setAttribute("quiz", newQuiz);
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					newQuiz.setContent(content);
					newQuiz.setAns1(choice1);
					newQuiz.setAns2(choice2);
					newQuiz.setAns3(choice3);
					newQuiz.setAns4(choice4);
					req.setAttribute("quiz", newQuiz);
					req.getRequestDispatcher(next).forward(req, res);
				}

			}
			break;
			case "updateQuiz":{
				jsp = "/jsp/" + sess.getAttribute("role") + ".jsp";
				db.updateQuiz(newQuiz);
				req.setAttribute("notice", "更新成功。");
				req.getRequestDispatcher(jsp).forward(req, res);
			}
		}
		return jsp;
	}
	public ArrayList<Quiz> convert(String book,String chap,ArrayList<String> file,int num){
		ArrayList<Quiz> quizAr = new ArrayList<Quiz>(file.size());
		Quiz q = null;
		int cnt = num + 1;
		for(String s : file) {
			q = new Quiz();
			q.setqCode("" + cnt);
			q.setlCode(chap);
			q.setbCode(book);
			String[] qDetails = s.split(",");
			q.setsCode(qDetails[0]);
			q.setContent(qDetails[1]);
			q.setAns1(qDetails[2]);
			q.setAns2(qDetails[3]);
			q.setAns3(qDetails[4]);
			q.setAns4(qDetails[5]);
			cnt++;
			quizAr.add(q);
		}
		return quizAr;

	}
}
