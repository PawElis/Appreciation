package servlets;

/*
  Created by Pavel on 28.10.2017.
  Servlet to page account: application authentication, user authorization, JSON obtaining,
  JSON processing, and obtaining data about latitude, longitude, and evaluation.
 */

import bean.ConnectorBean;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static functional.MapsPreparation.removeAllAttr;
import static functional.MapsPreparation.setAllUsersAttr;

@WebServlet(name = "AccountServlet")
public class AccountServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ConnectorBean.class);
    private static ConnectorBean connectorBean;


    @Override
    public void init(ServletConfig config) throws ServletException {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("applicationContext");
        config.getServletContext().setAttribute("AccountServlet", this);
        connectorBean = (ConnectorBean) context.getBean("connectorBean");
    }

    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            removeAllAttr(session);
            setAllUsersAttr(session, connectorBean, request);
            request.getRequestDispatcher("/account.jsp").forward(request, response);
        } catch (NullPointerException e) {
            log.warn("AccountServlet Exception. Check attributes and connection to VK.", e);
            request.getRequestDispatcher("/index").forward(request, response);
        }
    }
}