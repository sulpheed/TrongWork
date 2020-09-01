package control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MainControl2
 */
@WebServlet("/osakanhgo/*")
public class MainControl extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainControl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		Action act = null;
        act = getAction(request.getRequestURI());
        try {
        	String jsp = act.perform(request, response);
        	System.out.println(jsp);
        	if ( jsp.startsWith("/") ) {
                RequestDispatcher rd = getServletContext().getRequestDispatcher(jsp);
                rd.forward( request, response );
            } else {
            	response.sendRedirect(jsp);
            }
        }catch(Exception  e){

        }


	}

	private Action getAction( String path ) {
    	// 適当・・・
    	System.out.println(path);
        Action act = null;
        if ( path.equals("/N1data/osakanhgo/viewInfo")) {
            act = new UserInfoPage();
        } else if ( path.equals("/N1data/osakanhgo/noauth.do")) {
            //act = new NoAuthAction();
        } else if ( path.equals("/N1data/osakanhgo/logout.do")) {
            act = new LogoutAction();
        } else if (path.equals("/N1data/osakanhgo/login.do")) {
        	act = new LoginAction();
        }else if (path.equals("/N1data/osakanhgo/PassChange")) {
        	act = new ChangePassword();
        }else if (path.equals("/N1data/osakanhgo/searchTeacherAction")) {
        	act = new SearchTeacherAction();
        }else if (path.equals("/N1data/osakanhgo/UpgradeAction")) {
        	act = new UpgradeAction();
        }else if (path.equals("/N1data/osakanhgo/DowngradeAction")) {
        	act = new DowngradeAction();
        }else if (path.equals("/N1data/osakanhgo/SearchByNumber")) {
        	act = new SearchByNumber();
        }else if (path.equals("/N1data/osakanhgo/PassResetAction")) {
        	act = new PassResetAction();
		}else if (path.equals("/N1data/osakanhgo/csvReader.do")) {
	    	act = new Register();
	    }else if (path.equals("/N1data/osakanhgo/getClass")) {
	    	act = new classRegister();
	    }else if (path.equals("/N1data/osakanhgo/QuestVault")) {
	    	act = new GetBook();
	    }else if (path.equals("/N1data/osakanhgo/goTop")) {
	    	act = new Direction();
	    }else if (path.equals("/N1data/osakanhgo/getTest")) {
	    	act = new GetTest();
	    }else if (path.equals("/N1data/osakanhgo/doTest")) {
	    	act = new doTest();
	    }
        act.setControlServlet(this);
        return act;
    }

}