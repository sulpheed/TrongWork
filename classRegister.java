package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection;
import dao.csvReader;
import model.Student;

public class classRegister extends Action{
	public static HashMap<String,ArrayList<Student>> listOfClass = null;
	public static ArrayList<Student> newList = null;
	public static ArrayList<String> temp = null;
	@Override
	public String perform(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException, Exception {
		String link = req.getParameter("avatar");
		String code = req.getParameter("code");
		System.out.println("code=" + code);
		String next,stay,notice,jsp="";

		DBConnection db = new DBConnection();
		csvReader rd = new csvReader();
		switch(code) {
			case "read":{
				stay = "../html/uw16 classReg.html";
				jsp = "/jsp/newClassPreview.jsp";
				if("".equals(link)) {
					res.sendRedirect(stay);
				}else {
					try {
						temp = new ArrayList<>();
						temp = rd.readFile(link);
						ArrayList<Student> errList = sameStudent(convert(temp));
						ArrayList<String> nameOfClass = getClass(convert(temp));
						listOfClass = makeClassList(temp);
						if(errList == null) {
							newList = convert(temp);
							req.setAttribute("nameOfClass", nameOfClass);
							req.getRequestDispatcher(jsp).forward(req, res);
						}else {
							jsp = "/jsp/newClassErr.jsp";
							System.out.println(errList.get(0).getUserNumber());
							for(String k:listOfClass.keySet()) {
								for(Student S:listOfClass.get(k)) {
									for(Student s:errList) {
										if(S.getUserNumber().equals(s.getUserNumber())) {
											S.setUserNumber("<font color='red'>" + s.getUserNumber() + "</font>");
											nameOfClass.set(nameOfClass.indexOf(k), "<font color='red'>" + nameOfClass.get(nameOfClass.indexOf(k)) + "</font>");
										}
									}
								}
							}
							req.setAttribute("notice", "赤くなっているものはゆーざIDが重複または存在していない。");
							req.setAttribute("nameOfClass", nameOfClass);
							req.getRequestDispatcher(jsp).forward(req, res);
						}
					}catch(Exception e) {
						req.setAttribute("notice", "読み込みエラー");
						req.setAttribute("target",stay );
						req.getRequestDispatcher(jsp).forward(req, res);
					}
				}
			}
			break;
			case "details":{
				jsp = "/jsp/classView.jsp";
				String name = req.getParameter("ID");
					System.out.println(name);
					if(listOfClass.containsKey(name)) {
						req.setAttribute("nameOfClass", name);
						req.setAttribute("classView", listOfClass.get(name));
						req.getRequestDispatcher(jsp).forward(req, res);
					}
			}
			break;
			case "save":{
				stay = "/jsp/newClassPreview.jsp";
				next = "../html/classRegSuccess.html";
				jsp = "/jsp/noticePage.jsp";
				ArrayList<Student> errList = null;
				try {
					for(int i = 0;i < newList.size();i++) {
						if(!db.checkStudent(newList.get(i))) {
							if(errList == null) errList = new ArrayList<Student>();
							errList.add(newList.get(i));
						}
					}
					if(errList == null) {
						db.updateClass(newList);
						newList = null;
						req.setAttribute("notice", "クラス更新成功");
						req.setAttribute("target", next);
						req.getRequestDispatcher(jsp).forward(req, res);
					}else {
						jsp = "/jsp/newClassErr.jsp";
						ArrayList<String> nameOfClass = getClass(convert(temp));
						System.out.println(errList.get(0).getUserNumber());
						for(String k:listOfClass.keySet()) {
							for(Student S:listOfClass.get(k)) {
								for(Student s:errList) {
									if(S.getUserNumber().equals(s.getUserNumber())) {
										S.setUserNumber("<font color='red'>" + s.getUserNumber() + "</font>");
										nameOfClass.set(nameOfClass.indexOf(k), "<font color='red'>" + nameOfClass.get(nameOfClass.indexOf(k)) + "</font>");
									}
								}
							}
						}
						req.setAttribute("notice", "赤くなっているものはゆーざIDが重複または存在していない。");
						req.setAttribute("nameOfClass", nameOfClass);
						req.getRequestDispatcher(jsp).forward(req, res);
					}
				}catch(Exception e) {
					req.setAttribute("notice", "クラス更新成功");
					req.setAttribute("target", stay);
					req.getRequestDispatcher(jsp).forward(req, res);
				}
			}
		}
		return jsp;
	}
	public ArrayList<Student> convert(ArrayList<String> ary){
		ArrayList<Student> ary2 = new ArrayList<>(ary.size());
		for(String s:ary) {
			String[] temp = s.split(",");
			if(temp.length < 4) {
				Student u = new Student();
				u.setUserNumber(temp[1]);
				u.setName(temp[2]);
				u.setClassName(temp[0]);;
				ary2.add(u);
			}
		}
		return ary2;
	}
	public HashMap<String,ArrayList<Student>> makeClassList(ArrayList<String> ary){
		ArrayList<Student> stuList = convert(ary);
		HashMap<String,ArrayList<Student>> classMap = new HashMap<String,ArrayList<Student>>();
		ArrayList<String> className = getClass(stuList);
		for(String c:className) {
			classMap.put(c,new ArrayList<Student>());
		}
		for(Student s:stuList) {
			if(classMap.containsKey(s.getClassName())) {
				classMap.get(s.getClassName()).add(s);
			}
		}
		return classMap;
	}
	public ArrayList<String> getClass(ArrayList<Student> ar) {
		ArrayList<String> className = new ArrayList<String>();
		Collections.sort((List<Student>)ar,new SortByClass());
		String tmp = ar.get(0).getClassName();
		className.add(tmp);
		for(int i = 0;i < ar.size() - 1;i++) {
			if(!ar.get(i).getClassName().equals(ar.get(i + 1).getClassName())) {
				tmp = ar.get(i + 1).getClassName();
				className.add(tmp);
			}
		}
		return className;
	}
	public ArrayList<Student> sameStudent(ArrayList<Student> ary){
		ArrayList<Student> err = null;
		TitleCheck ch = new TitleCheck();
		for(int i = 0;i < ary.size() - 1;i++) {
			int kengen = ch.checkRole(ary.get(i));
			for(int j = i + 1;j < ary.size();j++) {
				if(ary.get(i).getUserNumber().equals(ary.get(j).getUserNumber()) || kengen != 0) {
					if(err == null) err = new ArrayList<Student>();
					err.add(ary.get(i));
				}
			}
		}
		return err;
	}
}
class SortByClass implements Comparator<Student>{

	@Override
	public int compare(Student o1, Student o2) {
		if ( o1.getClassName().compareTo(o2.getClassName()) < 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
