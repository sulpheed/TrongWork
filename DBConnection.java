package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.AccLogin;
import model.Book;
import model.Chap;
import model.Quiz;
import model.Result;
import model.Student;
import model.Test;
import model.User;

public class DBConnection {
	private Connection con;
	private String server = "jdbc:mysql://localhost:3306/forum?characterEncoding=UTF-8";		//DBã‚µãƒ¼ãƒ�å��
	private String user = "sulpheed";		//ãƒ¦ãƒ¼ã‚¶å��
	private String pass = "trong4588";		//ãƒ‘ã‚¹ãƒ¯ãƒ¼ãƒ‰
	public void createConnection() throws SQLException{
		con = DriverManager.getConnection(server, user , pass);
		//è‡ªå‹•ã‚³ãƒŸãƒƒãƒˆãƒ¢ãƒ¼ãƒ‰ã�®è§£é™¤
		con.setAutoCommit(false);
	}
	public void closeConnection() throws SQLException{
		if(con != null) {
			con.close();
		}
	}
	public User login(AccLogin acc) throws Exception{
		User user = null;
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");

			} catch (Exception e) {
				// TODO è‡ªå‹•ç”Ÿæˆ�ã�•ã‚Œã�Ÿ catch ãƒ–ãƒ­ãƒƒã‚¯
				e.printStackTrace();
			}
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select * from user where USERNUMBER = ? and password = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,acc.getUserNumber());
			stmt.setString(2,acc.getPassword());
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			while(res.next()) {
				user = new User();
				user.setUserNumber(res.getString(1));
				user.setName(res.getString(2));
				user.setPassword(res.getString(5));
				user.setEmail(res.getString(3));
				user.setRole(res.getString(4));
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return user;
	}
	public Student getInfo(String id) throws Exception{
		Student st = null;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select * from student where USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,id);
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			while(res.next()) {
				st = new Student();
				st.setLevel(res.getInt(2));
				st.setVocabulary(res.getInt(3));
				st.setGrammar(res.getInt(4));
				st.setReading(res.getInt(5));
				st.setLink(res.getString(6));
				st.setClassName(res.getString(7));
				st.setCertificate(res.getString(8));
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return st;
	}
	public boolean Authorized(String user,String pass) {
		boolean check = false;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select USERNUMBER,password from user where USERNUMBER = ? && password = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,user);
			stmt.setString(2,pass);
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			if(res.next()) {
				check = true;
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return check;
	}
	public void changePass(String user,String pass) {
		try {
			createConnection();
			//Statement rs = null;
			PreparedStatement stmt = null;
			String sql = "update user set password = ? where USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,pass);
			stmt.setString(2,user);
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			int c = stmt.executeUpdate();
			con.commit();
			closeConnection();
			if(c != 0) {
				System.out.println("password has changed!");
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public User searchTeacher(String name) throws Exception{
		User user = null;
		try {
			createConnection();
			//Statement rs = null;
			String mei = "%" + name + "%";
			System.out.println(mei);
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select * from user where fullname like ? or fullname like ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,mei);
			stmt.setString(2,mei.replaceAll("[ |ã€€]", ""));
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			while(res.next()) {
				user = new User();
				user.setUserNumber(res.getString(1));
				user.setName(res.getString(2));
				//user.setPassword(res.getString(3));
				//user.setEmail(res.getString(4));
				user.setRole(res.getString(5));
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return user;
	}
	public void upgrade(String id) throws Exception{
		try {
			createConnection();
			//Statement rs = null;
			//ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "UPDATE user SET role = 'manager' WHERE USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,id);
			int cnt = stmt.executeUpdate();

			con.commit();
			if(cnt != 0) System.out.println("1ä»¶ã�Œå¤‰ã‚�ã‚Šã�¾ã�—ã�Ÿã€‚");
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void downgrade(String id) throws Exception{
		try {
			createConnection();
			//Statement rs = null;
			//ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "UPDATE user SET role = 'teacher' WHERE USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,id);
			int cnt = stmt.executeUpdate();

			con.commit();
			if(cnt != 0) System.out.println("1ä»¶ã�Œå¤‰ã‚�ã‚Šã�¾ã�—ã�Ÿã€‚");
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void passReset(String number) throws Exception{
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "UPDATE user SET password = USERNUMBER WHERE USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,number);
			stmt.executeUpdate();

			con.commit();
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public User searchByNumber(String number) {
		User user = null;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select * from user where USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,number);
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			while(res.next()) {
				user = new User();
				user.setUserNumber(res.getString(1));
				user.setName(res.getString(2));
				user.setPassword(res.getString(3));
				user.setEmail(res.getString(4));
				user.setRole(res.getString(5));
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return user;
	}
	public ArrayList<Student> searchByClass(String temp){
		ArrayList<Student> list = null;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select * from studentinfo where classcode = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1,temp);
			//rs = con.createStatement();
			//rs.executeUpdate(sql);
			res = stmt.executeQuery();
			while(res.next()) {
				if(list == null) list = new ArrayList<Student>();
				Student stu = new Student();
				stu.setUserNumber(res.getString(1));
				stu.setName(res.getString(2));
				stu.setEmail(res.getString(3));
				stu.setRole(res.getString(4));
				stu.setLevel(res.getInt(5));
				stu.setVocabulary(res.getInt(6));
				stu.setGrammar(res.getInt(7));
				stu.setReading(res.getInt(8));
				stu.setClassName(res.getString(9));
				stu.setCertificate(res.getString(10));
				list.add(stu);
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}

		return list;

	}
	public void Register(User newUser) throws Exception{
		try {
			createConnection();
			//Statement rs = null;
			//ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "insert into user values(?,?,?,'',?)";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, newUser.getUserNumber());
				stmt.setString(2, newUser.getName());
				stmt.setString(3, newUser.getUserNumber());
				stmt.setString(4, newUser.getRole());
				//rs = con.createStatement();
				//rs.executeUpdate(sql);
				stmt.execute();
				con.commit();
				if("student".equals(newUser.getRole())) {
					String sql1 = "SET FOREIGN_KEY_CHECKS=0";
					String sql2 = "insert into student values(?,0,'0','0','0','','','')";
					String sql3 =  "SET FOREIGN_KEY_CHECKS=1";
					System.out.println("check " + newUser.getRole());
					stmt = con.prepareStatement(sql1);
					stmt.execute();
					stmt = con.prepareStatement(sql2);
					stmt.setString(1, newUser.getUserNumber());
					stmt.execute();
					con.commit();
					stmt = con.prepareStatement(sql3);
					stmt.execute();
				}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void studentReg(User newUser) throws Exception{
		try {
			createConnection();
			//Statement rs = null;
			//ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql1 = "SET FOREIGN_KEY_CHECKS=0";
			String sql2 = "insert into student values(?,0,'0','0','0','','','')";
			String sql3 =  "SET FOREIGN_KEY_CHECKS=1";
			System.out.println("check " + newUser.getRole());
			stmt = con.prepareStatement(sql1);
			stmt.execute();
			stmt = con.prepareStatement(sql2);
			stmt.setString(1, newUser.getUserNumber());
			stmt.execute();
			con.commit();
			stmt = con.prepareStatement(sql3);
			stmt.execute();
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public boolean check(User user) {
		boolean err = false;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select count(*)\r\n" +
					"from user\r\n" +
					"where USERNUMBER = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,user.getUserNumber());

				//rs = con.createStatement();
				//rs.executeUpdate(sql);
				res = stmt.executeQuery();
				int cnt = 0;
				while(res.next()) {
					cnt = res.getInt(1);
				}
				if(cnt != 0) {
					err = true;
				}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return err;
	}
	public boolean checkStudent(Student stu) {
		boolean err = false;
		try {
			createConnection();
			//Statement rs = null;
			ResultSet         res  = null;
			PreparedStatement stmt = null;
			String sql = "select count(*)\r\n" +
					"from student\r\n" +
					"where USERNUMBER = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,stu.getUserNumber());

				//rs = con.createStatement();
				//rs.executeUpdate(sql);
				res = stmt.executeQuery();
				int cnt = 0;
				while(res.next()) {
					cnt = res.getInt(1);
				}
				if(cnt != 0) {
					err = true;
				}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return err;
	}
	public void updateClass(ArrayList<Student> ar) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "UPDATE student SET class = ? WHERE USERNUMBER = ?";
			for(int i = 0;i < ar.size();i++) {
				stmt = con.prepareStatement(sql);
				stmt.setString(1,ar.get(i).getClassName());
				stmt.setString(2,ar.get(i).getUserNumber());
				stmt.executeUpdate();

				con.commit();
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public ArrayList<Book> getBook() throws SQLException {
		ArrayList<Book> B = null;
		createConnection();
		//Statement rs = null;
		ResultSet         res  = null;
		ResultSet         res2  = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		String sql1 = "select * from book";
		String sql2 = "select * from lesson where bookcode = ?";
		stmt = con.prepareStatement(sql1);
		//rs = con.createStatement();
		//rs.executeUpdate(sql);
		res = stmt.executeQuery();
		while(res.next()) {
			ArrayList<Chap> chap = null;
			if(B == null) B = new ArrayList<Book>();
			Book b = new Book();
			b.setName(res.getString(2));
			b.setChapNumber(res.getInt(3));
			stmt2 = con.prepareStatement(sql2);
			stmt2.setString(1, b.getName());
			res2 = stmt2.executeQuery();
			while(res2.next()) {
				if(chap == null) chap = new ArrayList<>(b.getChapNumber());
				Chap c = new Chap();
				c.setChapID(res2.getString(1));
				c.setChapName(res2.getString(3));
				chap.add(c);
			}
			b.setChapList(chap);
			B.add(b);
		}
		closeConnection();
		if(stmt != null) {
			stmt.close();
		}
		return B;
	}
	public void saveBook(Book b) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql1 = "insert into book values(?,?,?)";
			stmt = con.prepareStatement(sql1);
			stmt.setString(1,b.getName() );
			stmt.setString(2,b.getName() );
			stmt.setInt(3,b.getChapNumber() );
			stmt.execute();
			con.commit();
			String sql2 = "insert into lesson values(?,?,?)";
			for(int i = 0;i < b.getChapNumber();i++) {
				stmt = con.prepareStatement(sql2);
				stmt.setString(1,b.getChapList().get(i).getChapID() );
				stmt.setString(2,b.getName());
				stmt.setString(3,b.getChapList().get(i).getChapName());
				stmt.execute();
				con.commit();
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void saveQuiz(Quiz q) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "insert into question values(?,?,?,?,?,?,?,?,?)";
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=0").execute();
			stmt = con.prepareStatement(sql);
			stmt.setString(1,q.getqCode() );
			stmt.setString(2,q.getsCode() );
			stmt.setString(3,q.getlCode() );
			stmt.setString(4,q.getbCode() );
			stmt.setString(5,q.getContent() );
			stmt.setString(6,q.getAns1() );
			stmt.setString(7,q.getAns2() );
			stmt.setString(8,q.getAns3() );
			stmt.setString(9,q.getAns4() );
			stmt.execute();
			con.commit();
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=1").execute();
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public int getCount(String table) {
		int cnt = 0;
		try {
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select count(*) from " + table;
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) cnt = rs.getInt(1);
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return cnt;
	}
	public ArrayList<Quiz> searchQuiz(Quiz q){
		ArrayList<Quiz> qAr= null;
		Quiz t = null;
		try {
			String skill = "%" + q.getsCode() + "%";
			String lesson = "%" + q.getlCode() + "%";
			String book = "%" + q.getbCode() + "%";
			String content = "%" + q.getContent().replaceAll("\\s", "") + "%";
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select * from question where "
					+ "skillcode like ? and lessoncode like ? and bookcode like ? and content like ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, skill);
			stmt.setString(2, lesson);
			stmt.setString(3, book);
			stmt.setString(4, content);
			rs = stmt.executeQuery();
			while(rs.next()) {
				if(qAr == null) qAr = new ArrayList<Quiz>();
				t = new Quiz();
				t.setqCode(rs.getString(1));
				t.setsCode(rs.getString(2));
				t.setlCode(rs.getString(3));
				t.setbCode(rs.getString(4));
				t.setContent(rs.getString(5));
				t.setAns1(rs.getString(6));
				t.setAns2(rs.getString(7));
				t.setAns3(rs.getString(8));
				t.setAns4(rs.getString(9));
				qAr.add(t);
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return qAr;
	}
	public void updateQuiz(Quiz q) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "update question "
					+ "set content = ?,choice1 = ?,choice2 = ?,choice3 = ?,choice4 = ?"
					+ "where questioncode = ?";
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=0").execute();
			stmt = con.prepareStatement(sql);
			stmt.setString(1,q.getContent() );
			stmt.setString(2,q.getAns1() );
			stmt.setString(3,q.getAns2() );
			stmt.setString(4,q.getAns3() );
			stmt.setString(5,q.getAns4()  );
			stmt.setString(6,q.getqCode());
			stmt.executeUpdate();
			con.commit();
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=1").execute();
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void updateInfo(User u,String shikaku) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "update user set fullname = ?,mailaddress = ?"
					+ "where USERNUMBER = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, u.getName());
			stmt.setString(2, u.getEmail());
			stmt.setString(3, u.getUserNumber());
			stmt.executeUpdate();
			con.commit();
			if("student".equals(u.getRole())) {
				String sql2 = "update student set CERTIFICATE = ?"
						+ "where USERNUMBER = ?";
				stmt = con.prepareStatement(sql2);
				stmt.setString(1, shikaku);
				stmt.setString(2, u.getUserNumber());
				stmt.executeUpdate();
				con.commit();
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public Test getOneTest(String testID){
		Test t = null;
		try {
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select * from testdetails where testcode = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, testID);
			rs = stmt.executeQuery();
			while(rs.next()) {
				t = new Test();
				t.setTestCode(rs.getString(1));
				if(rs.getDate(2)!=null) {
					t.setStartDay((java.sql.Date)rs.getDate(2));
				}else {
					t.setStartDay(null);
				}
				if(rs.getDate(3)!=null) {
					t.setEndDay((java.sql.Date)rs.getDate(3));
				}else {
					t.setEndDay(null);
				}
				t.setType(rs.getString(4));
				t.setClassCode(rs.getString(5));
				t.setSkill(rs.getString(6));
				t.setBook(rs.getString(7));
				t.setLesson(rs.getString(8));
				t.setStatus(rs.getInt(9));
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return t;
	}
	public ArrayList<Test> getTest(){
		ArrayList<Test> ary = null;
		try {
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select * from testdetails";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				if(ary == null) ary = new ArrayList<Test>();
				Test t = new Test();
				t.setTestCode(rs.getString(1));
				if(rs.getDate(2)!=null) {
					t.setStartDay((java.sql.Date)rs.getDate(2));
				}else {
					t.setStartDay(null);
				}
				if(rs.getDate(3)!=null) {
					t.setEndDay((java.sql.Date)rs.getDate(3));
				}else {
					t.setEndDay(null);
				}
				t.setType(rs.getString(4));
				t.setClassCode(rs.getString(5));
				t.setSkill(rs.getString(6));
				t.setBook(rs.getString(7));
				t.setLesson(rs.getString(8));
				t.setStatus(rs.getInt(9));
				ary.add(t);
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return ary;
	}
	public int getTestNumber(String type) {
		int cnt = 0;
		try {
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select count(*) from testdetails where testtypecode = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, type);
			rs = stmt.executeQuery();
			while(rs.next()) cnt = rs.getInt(1);
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return cnt;
	}
	public void saveTest(Test t,ArrayList<Quiz> ar) {
		try {
			//ArrayList<Quiz> ar
			createConnection();
			PreparedStatement stmt = null;
			String sql1 = "insert into testdetails(testcode,testtypecode,classcode,skillcode,bookcode,lessoncode,status) "
					+ "values(?,?,?,?,?,?,?)";
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=0").execute();
			stmt = con.prepareStatement(sql1);
			stmt.setString(1, t.getTestCode());
			stmt.setString(2, t.getType());
			stmt.setString(3, t.getClassCode());
			stmt.setString(4, t.getSkill());
			stmt.setString(5, t.getBook());
			stmt.setString(6, t.getLesson());
			stmt.setInt(7, t.getStatus());
			stmt.execute();
			con.commit();
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=1").execute();
			String sql2 = "insert into testquiz values(?,?,?)";
			stmt = con.prepareStatement(sql2);
			int cnt = 1;
			for(Quiz q:ar) {
				stmt.setString(1, t.getTestCode());
				stmt.setInt(2, cnt++);
				stmt.setString(3, q.getqCode());
				stmt.execute();con.commit();
			}
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void updateTest(Test t,ArrayList<Quiz> ar) {
		try {
			//ArrayList<Quiz> ar
			createConnection();
			PreparedStatement stmt = null;
			String sql1 = "DELETE FROM testquiz WHERE testcode = ?";
			stmt = con.prepareStatement(sql1);
			stmt.setString(1, t.getTestCode());
			stmt.executeUpdate();
			con.commit();
			String sql2 = "insert into testquiz values(?,?,?)";
			stmt = con.prepareStatement(sql2);
			int cnt = 1;
			for(Quiz q:ar) {
				stmt.setString(1, t.getTestCode());
				stmt.setInt(2, cnt++);
				stmt.setString(3, q.getqCode());
				stmt.execute();con.commit();
			}
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public ArrayList<Quiz> getTestQuiz(String testID){
		ArrayList<Quiz> ar = null;
		try {
			createConnection();
			String sql = "select * from question where exists ("
					+ "select questioncode from testquiz where testcode = ? "
					+ "&& questioncode = question.questioncode)";
			PreparedStatement stmt = null;
			ResultSet rs = null;
			stmt = con.prepareStatement(sql);
			stmt.setString(1, testID);
			rs = stmt.executeQuery();
			while(rs.next()) {
				if(ar == null) ar = new ArrayList<Quiz>();
				Quiz t = new Quiz();
				t.setqCode(rs.getString(1));
				t.setsCode(rs.getString(2));
				t.setlCode(rs.getString(3));
				t.setbCode(rs.getString(4));
				t.setContent(rs.getString(5));
				t.setAns1(rs.getString(6));
				t.setAns2(rs.getString(7));
				t.setAns3(rs.getString(8));
				t.setAns4(rs.getString(9));
				ar.add(t);
			}
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return ar;
	}
	public void sendTest(Test t,ArrayList<Student> ar) {
		try {
			//ArrayList<Quiz> ar
			createConnection();
			PreparedStatement stmt = null;
			String sql = "insert into testresult values(?,?,?,?,?)";
			for(Student s:ar) {
				con.prepareStatement("SET FOREIGN_KEY_CHECKS=0").execute();
				stmt = con.prepareStatement(sql);
				stmt.setString(1, t.getTestCode());
				stmt.setString(2, s.getUserNumber());
				stmt.setString(3, null);
				stmt.setString(4, "");
				stmt.setInt(5, 0);
				stmt.execute();
				con.commit();
				con.prepareStatement("SET FOREIGN_KEY_CHECKS=1").execute();
			}
			if(stmt != null) {
				stmt.close();
			}

			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void updateTestDetails(Test t,ArrayList<Quiz> ar){
		try {
			//ArrayList<Quiz> ar
			createConnection();
			PreparedStatement stmt = null;
			String sql = "update testdetails "
					+ "set exammakedate = ?, expirydate = ?,status = ? "
					+ "where testcode = ?";
				stmt = con.prepareStatement(sql);
				stmt.setDate(1, t.getStartDay());
				stmt.setDate(2, t.getEndDay());
				stmt.setInt(3, t.getStatus());
				stmt.setString(4,t.getTestCode());
				stmt.executeUpdate();
				con.commit();
				String sql1 = "DELETE FROM testquiz WHERE testcode = ?";
				stmt = con.prepareStatement(sql1);
				stmt.setString(1, t.getTestCode());
				stmt.execute();
				con.commit();
				String sql2 = "insert into testquiz values(?,?,?)";
				stmt = con.prepareStatement(sql2);
				int cnt = 1;
				for(Quiz q:ar) {
					stmt.setString(1, t.getTestCode());
					stmt.setInt(2, cnt++);
					stmt.setString(3, q.getqCode());
					stmt.execute();con.commit();
				}
			if(stmt != null) {
				stmt.close();
			}

			closeConnection();
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public void saveTestDetails(Test t,ArrayList<Quiz> ar) {
		try {
			createConnection();
			PreparedStatement stmt = null;
					String sql1 = "insert into testdetails "
					+ "values(?,?,?,?,?,?,?,?,?)";
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=0").execute();
			stmt = con.prepareStatement(sql1);
			stmt.setString(1, t.getTestCode());
			stmt.setDate(2, t.getStartDay());
			stmt.setDate(3, t.getEndDay());
			stmt.setString(4, t.getType());
			stmt.setString(5, t.getClassCode());
			stmt.setString(6, t.getSkill());
			stmt.setString(7, t.getBook());
			stmt.setString(8, t.getLesson());
			stmt.setInt(9, t.getStatus());
			stmt.execute();
			con.commit();
			con.prepareStatement("SET FOREIGN_KEY_CHECKS=1").execute();
			String sql2 = "insert into testquiz values(?,?,?)";
			stmt = con.prepareStatement(sql2);
			int cnt = 1;
			for(Quiz q:ar) {
				stmt.setString(1, t.getTestCode());
				stmt.setInt(2, cnt++);
				stmt.setString(3, q.getqCode());
				stmt.execute();con.commit();
			}
			if(stmt != null) {
				stmt.close();
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
	public ArrayList<Result> getTestList(String userID) {
		ArrayList<Result> kekka = null;
		try {
			createConnection();
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "select * from testresult\r\n" +
					"where usernumber = ? and examdate is null";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, userID);
			rs = stmt.executeQuery();
			while(rs.next()) {
				if(kekka == null) kekka = new ArrayList<Result>();
				Result r = new Result();
				r.setTestcode(rs.getString(1));
				r.setDay(rs.getDate(3));
				r.setUserNumber(rs.getString(2));
				r.setResult(rs.getString(4));
				r.setGetExp(rs.getInt(5));
				kekka.add(r);
			}
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
		return kekka;
	}
	public void updateRs(Result rs) {
		try {
			createConnection();
			PreparedStatement stmt = null;
			String sql = "update testresult "
					+ "set examdate = ?,result = ?,getEXP = ? "
					+ "where testcode = ? and usernumber = ?";
			stmt = con.prepareStatement(sql);
			stmt.setDate(3, rs.getDay());
			stmt.setString(4, rs.getResult());
			stmt.setInt(5, rs.getGetExp());
			stmt.setString(1, rs.getTestcode());
			stmt.setString(2, rs.getUserNumber());
			stmt.executeUpdate();con.commit();
			closeConnection();
			if(stmt != null) {
				stmt.close();
			}
		}catch(SQLException e) {
			System.out.println("cannot connect");
		}
	}
}