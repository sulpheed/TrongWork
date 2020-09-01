package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;

public class UpgradeAction extends Action{

	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		// TODO 自動生成されたメソッド・スタブ
		String teacherID = req.getParameter("teacherID");
		String name = req.getParameter("fullName");
		String stay = "../html/uw06 upgradeSubmit.html";
		String next = "../html/uw06 upgradeSuccess.html";
		String jsp = "/jsp/updateAct2.jsp";
		DBConnection db = new DBConnection();
		TitleCheck ch = new TitleCheck();
		int role = ch.checkRole(SearchTeacherAction.userByName);
		if(role == 0 || role == 2) {
			req.setAttribute("notice", "アップグレードできない対象です");
			req.setAttribute("ID", teacherID);
			req.setAttribute("role", SearchTeacherAction.userByName.getRole());
			req.setAttribute("fullName", name);
			req.setAttribute("target",stay );
			req.getRequestDispatcher(jsp).forward(req, res);
		}else {
			try {
				db.upgrade(teacherID);
				req.setAttribute("notice", "管理者にアップグレード成功");
				req.setAttribute("ID", teacherID);
				req.setAttribute("role", "管理者");
				req.setAttribute("fullName", name);
				req.setAttribute("target",next );
				req.getRequestDispatcher(jsp).forward(req, res);
			}catch(Exception e) {
				req.setAttribute("notice", "接続エラー");
				req.setAttribute("target",stay );
				req.getRequestDispatcher(jsp).forward(req, res);
			}
		}
		return jsp;
	}

}
