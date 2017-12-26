package main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SpringApplicationContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        servletContextEvent.getServletContext().setAttribute("applicationContext", context);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
