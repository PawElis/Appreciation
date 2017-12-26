package servlets;


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
import static functional.MapsPreparation.setAllAttr;

/**
 * Created by Pavel on 23.11.2017.
 * Start web app
 */

@WebServlet(name = "StartServlet")
public class StartServlet extends HttpServlet {
    private static ConnectorBean connectorBean;
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("applicationContext");
        connectorBean = (ConnectorBean) context.getBean("connectorBean");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            removeAllAttr(session);
            setAllAttr(session, connectorBean);
        } catch (Exception e) {
            log.warn("StartServlet Exception. Check attributes and connectorBean.", e);
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
