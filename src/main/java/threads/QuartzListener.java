package threads;

/*
  Created by Pavel on 10.12.2017.
  Multithreading Scheduler
 */

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.*;

import bean.ConnectorBean;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class QuartzListener implements ServletContextListener {
    private Scheduler scheduler = null;
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContext) {
        log.info("Context Initialized.");
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("servletContext", servletContext.getServletContext());

            JobDetail job = newJob(QuartzJob.class).withIdentity(
                    "CronQuartzJob", "Group").usingJobData(jobDataMap).build();
            Trigger trigger = newTrigger()
                    .withIdentity("Minute", "Group")
                    .withSchedule(CronScheduleBuilder.cronSchedule("1 * * * * ?"))
                    .build();

            JobDetail jobCompression = newJob(QuartzJobCompression.class).withIdentity(
                    "CronQuartzJobCompression", "GroupCompression").usingJobData(jobDataMap).build();
            Trigger triggerCompression = newTrigger()
                    .withIdentity("EveryMount", "GroupCompression")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 ? * 6L"))
                    .build();

            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.deleteJob(job.getKey());
            scheduler.scheduleJob(job, trigger);
            scheduler.deleteJob(jobCompression.getKey());
            scheduler.scheduleJob(jobCompression, triggerCompression);
        } catch (SchedulerException e) {
            log.warn("Scheduler not initialized.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContext) {
        log.info("Context Destroyed.");
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.warn("Scheduler not shut down.", e);
        }
    }
}