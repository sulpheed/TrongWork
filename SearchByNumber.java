package control;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;
import model.Student;
import model.User;

public class SearchByNumber extends Action{
	public static User userByNumber;
	public static ArrayList<Student> stuList;
	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		String userID = req.getParameter("userNumber");
		String group = req.getParameter("group");
		String code = req.getParameter("code");
		DBConnection db = new DBConnection();
		String jsp="",stay,next;
		switch(code) {
			case "reset":{
				stay = "../html/uw07 passReset.html";
				next = "../html/uw07_1 passReset.html";
				jsp = "/jsp/resetPage.jsp";
				if("".equals(userID)) {
					req.setAttribute("notice", "未入力です。");
					req.setAttribute("target",stay );
					req.getRequestDispatcher(jsp).forward(req, res);
				}else {
					try {
						userByNumber = db.searchByNumber(userID);
						if(userByNumber != null) {
							req.setAttribute("notice", " ");
							req.setAttribute("target",next );
							req.setAttribute("fullName", userByNumber.getName());
							req.setAttribute("ID", userByNumber.getUserNumber());
							req.setAttribute("email", userByNumber.getEmail());
							System.out.println(userByNumber.getName());
							System.out.println(userByNumber.getUserNumber());
							System.out.println(userByNumber.getEmail());
							req.getRequestDispatcher(jsp).forward(req, res);
						}else {
							req.setAttribute("notice", "この番号が存在してない。");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
						}
					}catch(Exception e) {
						req.setAttribute("notice", "接続エラー");
						req.setAttribute("target",stay );
						req.getRequestDispatcher(jsp).forward(req, res);
					}
				}
				break;
			}
			case "search":{
				stay = "../html/uw08 findStudent.html";
				next = "../html/uw08 studentInfo.html";
				jsp = "/jsp/uw01_1 user.jsp";
				if("".equals(userID)) {
					req.setAttribute("notice", "未入力です。");
					req.setAttribute("target",stay );
					req.getRequestDispatcher(jsp).forward(req, res);
				}else {
					try {
						userByNumber = db.searchByNumber(userID);
						if(userByNumber != null) {
							TitleCheck ch = new TitleCheck();
							int kengen = ch.checkRole(userByNumber);
							if(kengen != 0) {
								req.setAttribute("notice", "学籍番号のみ。");
								req.setAttribute("target",stay );
								req.getRequestDispatcher(jsp).forward(req, res);
							}else {
								req.setAttribute("notice", "");
								req.setAttribute("target",next );
								Student student = new Student();
								student = db.getInfo(userByNumber.getUserNumber());
								student.setName(userByNumber.getName());
								student.setUserNumber(userByNumber.getUserNumber());
								student.setEmail(userByNumber.getEmail());
								student.setRole(userByNumber.getRole());
								req.setAttribute("student", student);
								req.getRequestDispatcher(jsp).forward(req, res);
							}
						}else {
							req.setAttribute("notice", "この番号が存在してない。");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
						}
					}catch(Exception e) {
						req.setAttribute("notice", "接続エラー");
						req.setAttribute("target",stay );
						req.getRequestDispatcher(jsp).forward(req, res);
					}
				}
				break;
			}
			case "group":{
				stuList = new ArrayList<Student>();
				stay = "../html/uw08 findStudent.html";
				next = "../html/uw08 viewClass.html";
				jsp = "/jsp/getClass.jsp";
				try {
					System.out.println(group);
					stuList = db.searchByClass(group);
					req.setAttribute("target",next );
					req.setAttribute("list", stuList);
					req.getRequestDispatcher(jsp).forward(req, res);
				}catch(Exception e) {
					req.setAttribute("notice", "接続エラー");
					req.setAttribute("target",stay );
					req.getRequestDispatcher(jsp).forward(req, res);
				}
			}
		}
		return jsp;
	}
}
