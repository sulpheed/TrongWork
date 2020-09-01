package control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Action {
    private MainControl servlet;

    public void setControlServlet( MainControl servlet ) {
        this.servlet = servlet;
    }
    public MainControl getControlServlet() {
        return servlet;
    }
    public abstract String perform( HttpServletRequest req, HttpServletResponse res)
                                            throws IOException, ServletException,Exception ;
}