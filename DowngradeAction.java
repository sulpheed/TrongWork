package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;

public class DowngradeAction extends Action{

	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		String teacherID = req.getParameter("teacherID");
		String name = req.getParameter("fullName");
		String stay = "../html/uw05 downgradeSubmit.html";
		String next = "../html/uw05 downgradeSuccess.html";
		String jsp = "/jsp/updateAct2.jsp";
		DBConnection db = new DBConnection();
		TitleCheck ch = new TitleCheck();
		int role = ch.checkRole(SearchTeacherAction.userByName);
		if(role == 0 || role == 1) {
			req.setAttribute("notice", "対象なしです。");
			req.setAttribute("ID", teacherID);
			req.setAttribute("role", SearchTeacherAction.userByName.getRole());
			req.setAttribute("fullName", name);
			req.setAttribute("target",stay );
			req.getRequestDispatcher(jsp).forward(req, res);
		}else {
			try {
				db.downgrade(teacherID);
				req.setAttribute("notice", "ダウングレード成功。");
				req.setAttribute("ID", teacherID);
				req.setAttribute("role", "æ•™å“¡");
				req.setAttribute("fullName", SearchTeacherAction.userByName.getRole());
				req.setAttribute("target",next );
				req.getRequestDispatcher(jsp).forward(req, res);
			}catch(Exception e) {
				req.setAttribute("notice", "接続エラー。");
				req.setAttribute("target",stay );
				req.getRequestDispatcher(jsp).forward(req, res);
			}
		}
		return jsp;
	}

}
