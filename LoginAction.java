package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;
import model.AccLogin;
import model.Student;
import model.User;

public class LoginAction extends Action{
	public String perform(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException,Exception {
		AccLogin acc = new AccLogin();
		acc.setUserNumber(req.getParameter("userNumber"));
    	acc.setPassword(req.getParameter("password"));
    	System.out.println(acc.getUserNumber() + "  " + acc.getPassword());
    	DBConnection db = new DBConnection();
    	String url = null;
    	try {
    		//System.out.println("testing.....");
    		User user = db.login(acc);
    		if(user != null) {
			HttpSession hs = req.getSession();
			hs.setAttribute("fullName", user.getName());
			hs.setAttribute("ID", user.getUserNumber());
			hs.setAttribute("email", user.getEmail());
			hs.setAttribute("role", user.getRole());
			switch(user.getRole()) {
			case "student" :
				Student st = db.getInfo(user.getUserNumber());
				hs.setAttribute("level",st.getLevel());
				hs.setAttribute("vocabulary", st.getVocabulary());
				hs.setAttribute("grammar", st.getGrammar());
				hs.setAttribute("reading", st.getReading());
				hs.setAttribute("testLink", st.getLink());
				hs.setAttribute("clas", st.getClassName());
				hs.setAttribute("shikaku", st.getCertificate());
				hs.setAttribute("email", user.getEmail());
				url = "/jsp/student.jsp";
				break;
			case "teacher" :
				url = "/jsp/teacher.jsp";
				break;
			case "manager" :
				url = "/jsp/manager.jsp";
				break;
			}
    		}else {
    			req.setAttribute("loginErr", "IDとパスワードどちらか間違い。");
    			req.setAttribute("target", req.getRequestURI());	// å…ƒURLã‚’ã‚»ãƒƒãƒˆ
    			req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
    		}
		} catch (Exception e) {
			// ãƒ­ã‚°ã‚¤ãƒ³ã�—ã�¦ã�„ã�ªã�„ç‚ºã€�ãƒ­ã‚°ã‚¤ãƒ³ç”»é�¢ã�¸
			req.setAttribute("loginErr", "cannot connect");
			req.setAttribute("target", req.getRequestURI());	// å…ƒURLã‚’ã‚»ãƒƒãƒˆ
			req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
		}
    	return url;
    }
}
