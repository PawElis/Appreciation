package servlets;

import bean.ConnectorBean;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import functional.InformationHandler;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import threads.QuartzListener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Pavel on 12.12.2017.
 * Getting attr for Job
 */

@WebServlet(name = "JobServlet")
public class JobServlet extends HttpServlet {
    private static ConnectorBean connectorBean;
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = (ApplicationContext) config.getServletContext().getAttribute("applicationContext");
        connectorBean = (ConnectorBean) context.getBean("connectorBean");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            QuartzListener quartzListener = new QuartzListener();
            ServletContext source = getServletContext();
            TransportClient transportClient = HttpTransportClient.getInstance();
            VkApiClient vk = new VkApiClient(transportClient);

            //User authorization
            String redirectUrl = "http://localhost/auth";
            UserActor actor = InformationHandler.getUserActor(request.getParameter("code"), vk, redirectUrl);

            ServletContextEvent servletContext = new ServletContextEvent(source);
            servletContext.getServletContext().setAttribute("actor", actor);
            servletContext.getServletContext().setAttribute("connectorBean", connectorBean);
            quartzListener.contextInitialized(servletContext);
            request.getRequestDispatcher("/index").forward(request, response);
        } catch (Exception e) {
            request.getRequestDispatcher("/index").forward(request, response);
            log.warn("Authorization failed.", e);
        }
    }
}
