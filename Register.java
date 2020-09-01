package control;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;
import dao.csvReader;
import model.User;

public class Register extends Action{
	public static ArrayList<User> userList = null;

	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		String link = req.getParameter("avatar");
		String code = req.getParameter("code");
		String next,stay,jsp="";
		ArrayList<String> temp = null;
		DBConnection db = new DBConnection();
		csvReader rd = new csvReader();
		switch(code) {
			case "read":{
				stay = "../html/uw04 register.html";
				jsp = "/jsp/registerPreview.jsp";
				if("".equals(link)) {
					res.sendRedirect(stay);
				}else {
					try {
						temp = new ArrayList<>();
						temp = rd.readFile(link);
						userList = convert(temp);
						req.setAttribute("userList",userList);
						req.setAttribute("notice", "以上の情報を登録してよろしいですか？");
						req.getRequestDispatcher(jsp).forward(req, res);
					}catch(Exception e) {
						System.out.println("読み込みエラー");
					}
				}
			}
			break;
			case "save":{
				next = "../html/uw04 registerSuccess.html";
				jsp = "/jsp/noticePage.jsp";
				ArrayList<Integer> userErr = null;
				for(User u:userList) {
					if(userErr == null) userErr = new ArrayList<Integer>();
					if(db.check(u)) userErr.add(userList.indexOf(u));
				}
				if(userErr.size() != 0) {
					stay = "/jsp/registerErr.jsp";
					req.setAttribute("notice", "赤くなっているものは重複です。");
					req.setAttribute("userErr", userErr);
					req.setAttribute("userList",userList);
					userList = null;
					req.getRequestDispatcher(stay).forward(req, res);
				}else {
					try {
						int cnt = 0;
						for(User u:userList) {
							db.Register(u);
							cnt++;
						}
						req.setAttribute("notice",cnt + "件の新規を登録成功");
						req.setAttribute("target",next);
						userList = null;
						req.getRequestDispatcher(jsp).forward(req, res);
					}catch(Exception e) {
						stay = "/jsp/registerPreview.jsp";
						req.setAttribute("notice", "接続エラー");
						req.setAttribute("target",stay);
						req.getRequestDispatcher(jsp).forward(req, res);
					}
				}
			}
			break;
		}
		return null;
	}
	public ArrayList<User> convert(ArrayList<String> ary){
		ArrayList<User> ary2 = new ArrayList<User>(ary.size());
		for(String s:ary) {
			String[] temp = s.split(",");
			if(temp.length < 4) {
				User u = new User();
				u.setUserNumber(temp[0]);
				u.setName(temp[1]);
				u.setPassword(temp[0]);
				u.setRole(temp[2]);
				ary2.add(u);
			}
		}
		return ary2;
	}

}
