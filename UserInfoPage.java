package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;
import model.Student;
import model.Teacher;
import model.User;
public class UserInfoPage extends Action{

	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		HttpSession hs = req.getSession();
		String url = null;
		String role = hs.getAttribute("role").toString();
		String next;
		String code = req.getParameter("code");
		DBConnection db = new DBConnection();
		if(code == null) {
			switch(role) {
			case ("student") :
				next = "../html/uw01_1 user.html";
				Student student = new Student();
				String name = hs.getAttribute("fullName").toString();
				int level = (int)hs.getAttribute("level");
				int vocabulary = (int)hs.getAttribute("vocabulary");
				int grammar = (int)hs.getAttribute("grammar");
				int reading = (int)hs.getAttribute("reading");
				String link = hs.getAttribute("testLink").toString();
				String clas = hs.getAttribute("clas").toString();
				String shikaku = hs.getAttribute("shikaku").toString();
				String mail = hs.getAttribute("email").toString();
				student.setLevel(level);
				student.setName(name);
				student.setCertificate(shikaku);
				student.setClassName(clas);
				student.setVocabulary(vocabulary);
				student.setGrammar(grammar);
				student.setReading(reading);
				student.setEmail(mail);
				student.setLink(link);
				req.setAttribute("student", student);
				req.setAttribute("target", next);
				url = "/jsp/uw01_1 user.jsp";

			break;
			default:
				next = "../html/uw01_1 tUser.html";
				Teacher teacher = new Teacher();

				teacher.setName(hs.getAttribute("fullName").toString());
				teacher.setEmail(hs.getAttribute("email").toString());
				//System.out.println(hs.getAttribute("fullName").toString() + hs.getAttribute("email").toString());
				req.setAttribute("teacher", teacher);
				req.setAttribute("target", next);
				url = "/jsp/uw01_2 tUser.jsp";
			}
		}else if("change".equals(code)){
			switch(role) {
				case ("student") :{
					next = "../html/uw01_1 editUser.html";
					Student student = new Student();
					String name = hs.getAttribute("fullName").toString();
					int level = (int)hs.getAttribute("level");
					int vocabulary = (int)hs.getAttribute("vocabulary");
					int grammar = (int)hs.getAttribute("grammar");
					int reading = (int)hs.getAttribute("reading");
					String link = hs.getAttribute("testLink").toString();
					String clas = hs.getAttribute("clas").toString();
					String shikaku = hs.getAttribute("shikaku").toString();
					String mail = hs.getAttribute("email").toString();
					student.setLevel(level);
					student.setName(name);
					student.setCertificate(shikaku);
					student.setClassName(clas);
					student.setVocabulary(vocabulary);
					student.setGrammar(grammar);
					student.setReading(reading);
					student.setEmail(mail);
					student.setLink(link);
					req.setAttribute("student", student);
					req.setAttribute("target", next);
					url = "/jsp/uw01_1 user.jsp";
				}
				break;
				default:{
					next = "../html/uw01_1 editTUser.html";
					Teacher teacher = new Teacher();
					teacher.setName(hs.getAttribute("fullName").toString());
					teacher.setEmail(hs.getAttribute("email").toString());
					req.setAttribute("teacher", teacher);
					req.setAttribute("target", next);
					url = "/jsp/uw01_2 tUser.jsp";
				}
			}
		}else if("saveChange".equals(code)) {
			next = "../html/uw02 editInfoOk.html";
			String name = req.getParameter("name");
			String mail = req.getParameter("mail");
			String shikaku = req.getParameter("shikaku");
			hs.setAttribute("email", mail);
			hs.setAttribute("fullName", name);
			hs.setAttribute("shikaku", shikaku);
			User u = new User();
			u.setUserNumber(hs.getAttribute("ID").toString());
			u.setName(name);
			u.setEmail(mail);
			u.setRole(hs.getAttribute("role").toString());
			db.updateInfo(u, shikaku);
			req.setAttribute("notice", "ユーザ情報が変更されました。");
			req.setAttribute("target", next);
			url = "/jsp/noticePage.jsp";

		}
		return url;
	}

}
