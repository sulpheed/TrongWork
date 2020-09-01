package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutAction extends Action {

    public String perform(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

    	// ログアウトの定型処理
    	HttpSession session = req.getSession();
    	session.invalidate();
    	session = null;

    	return "/jsp/login.jsp";
    }
}
