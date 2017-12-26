package threads;

/*
  Created by Pavel on 10.12.2017.
  Job to collect information
 */


import bean.ConnectorBean;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import functional.InformationHandler;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.sql.SQLException;


public class QuartzJob implements Job {
    private static final Logger log = Logger.getLogger(ConnectorBean.class);
    private InformationHandler informationHandler;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        ServletContext servletContext = (ServletContext) context.getMergedJobDataMap().get("servletContext");
        UserActor actor = (UserActor) servletContext.getAttribute("actor");
        ConnectorBean connectorBean = (ConnectorBean) servletContext.getAttribute("connectorBean");
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        try {
            informationHandler = new InformationHandler();
        } catch (IOException e) {
            log.warn("Could not create InformationHandler.", e);
        }
        try {
            informationHandler.getFriends(connectorBean, informationHandler, vk, actor);
        } catch (SQLException | IOException e) {
            log.warn("Could not get freinds.", e);
        }
    }
}