package threads;

/*
  Created by Pavel on 10.12.2017.
  Job to collect information
 */

import bean.ConnectorBean;
import functional.DataCompression;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.ServletContext;


public class QuartzJobCompression implements Job {
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        ServletContext servletContext = (ServletContext) context.getMergedJobDataMap().get("servletContext");
        ConnectorBean connectorBean = (ConnectorBean) servletContext.getAttribute("connectorBean");
        int highLevel = 100000;
        try {
            DataCompression.mergePlacesAppreciation(connectorBean, highLevel);
        } catch (Exception e){
            log.warn("Could not compress data.", e);
        }
    }
}