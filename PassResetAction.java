package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;
import model.User;

public class PassResetAction extends Action{
	public String perform(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException,Exception {
		DBConnection db = new DBConnection();
		String jsp,next;
		//stay = "../html/uw07_1 passReset.html";
		next = "../html/uw07_2 passReset.html";
		jsp = "/jsp/resetPage.jsp";
		db.passReset(SearchByNumber.userByNumber.getUserNumber());

		req.setAttribute("notice", "パスワードがリセットされた。");
		req.setAttribute("target",next );
		req.setAttribute("fullName", SearchByNumber.userByNumber.getName());
		req.setAttribute("ID", SearchByNumber.userByNumber.getUserNumber());
		req.setAttribute("email", SearchByNumber.userByNumber.getEmail());
		SearchByNumber.userByNumber = new User();
		req.getRequestDispatcher(jsp).forward(req, res);

    	return jsp;
    }
}
