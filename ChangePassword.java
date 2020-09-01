package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DBConnection;

public class ChangePassword extends Action{

	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		HttpSession sess = req.getSession();
		String jsp = "/jsp/noticePage.jsp";
		if(sess.getAttribute("role") == null) {
			jsp = "/jsp/login.jsp";
		}else {
			String stay = "../html/uw03_1 passChange.html";
			String next = "../html/uw03_1 passSave.html";
			String old = req.getParameter("Password1");
			String new1 = req.getParameter("Password2");
			String new2 = req.getParameter("Password3");
			if(old.equals("")||new1.equals("")||new2.equals("")) {
				req.setAttribute("notice", "未入力項目があります。");
				req.setAttribute("target",stay );
				req.getRequestDispatcher(jsp).forward(req, res);
			}else {
				if(!new1.equals(new2)) {
					req.setAttribute("notice", "新パスワード確認と新パスワードは違います");
					req.setAttribute("target",stay );
					req.getRequestDispatcher(jsp).forward(req, res);
				}else {
					TitleCheck chk = new TitleCheck();
					int pass = chk.checkPass(new1);
					switch(pass) {
						case 1:{
							req.setAttribute("notice", "パスワードの長さは8～32です");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
							break;
						}
						case 2:{
							req.setAttribute("notice", "大文字を含んでください。");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
							break;
						}
						case 3:{
							req.setAttribute("notice", "数字を含んでください");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
							break;
						}
						case 4:{
							req.setAttribute("notice", "大文字と数字が少なくとも一つずつを含んでください。");
							req.setAttribute("target",stay );
							req.getRequestDispatcher(jsp).forward(req, res);
							break;
						}
						case 0:{

							DBConnection dao = new DBConnection();
							try {
								if(!dao.Authorized(sess.getAttribute("ID").toString(), old)) {
									req.setAttribute("notice", "現在のパスワードは正しくない。");
									req.setAttribute("target",stay);
									req.getRequestDispatcher(jsp).forward(req, res);
								}else {
									dao.changePass(sess.getAttribute("ID").toString(), new1);
									req.setAttribute("notice", "パスワード変更成功。");
									req.setAttribute("target",next );
									req.getRequestDispatcher(jsp).forward(req, res);
								}
							}catch(Exception e) {
								req.setAttribute("notice", "接続エラー。");
								req.setAttribute("target",stay );
								req.getRequestDispatcher(jsp).forward(req, res);
							}
						}
					}
				}
			}
		}
		return jsp;
	}
}

