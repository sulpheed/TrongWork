package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;
import model.User;

public class SearchTeacherAction extends Action{
	public static User userByName;
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException,Exception {
		String teacher = req.getParameter("TeacherName");
		String manager = req.getParameter("ManagerName");
		String name,stay,next,notice;
		if(teacher==null) {
			name = manager;
			stay = "../html/uw05 downgrade.html";
			next = "../html/uw05 downgradeSubmit.html";
			notice = "こちらを教員にダウングレードしてよろしいですか。";
		}else {
			name = teacher;
			stay = "../html/uw06 upgrade.html";
			next = "../html/uw06 upgradeSubmit.html";
			notice = "こちらを管理者にアップグレードしてよろしいですか。";
		}
		DBConnection db = new DBConnection();
		String jsp = "/jsp/updateAct.jsp";
		if("".equals(name)) {
			req.setAttribute("notice", "未入力です");
			req.setAttribute("target",stay );
			req.getRequestDispatcher(jsp).forward(req, res);
		}else {
			try {
				userByName = db.searchTeacher(name);
				if(userByName != null) {
						req.setAttribute("notice", notice);
						req.setAttribute("target",next );
						req.setAttribute("ID", userByName.getUserNumber());
						System.out.println(userByName.getUserNumber());
						req.setAttribute("fullName", userByName.getName());
						System.out.println(userByName.getName());
						req.setAttribute("role", userByName.getRole());
						req.getRequestDispatcher(jsp).forward(req, res);

				}else {
					req.setAttribute("notice", "該当ユーザが存在しない。");
					req.setAttribute("target", stay);	// 元URLをセット
					req.getRequestDispatcher(jsp).forward(req, res);
				}
			} catch (Exception e) {
				// ログインしていない為、ログイン画面へ
				req.setAttribute("notice", "接続エラー");
				req.setAttribute("target", stay);	// 元URLをセット
				req.getRequestDispatcher(jsp).forward(req, res);
			}
		}
    	return jsp;
	}
}
