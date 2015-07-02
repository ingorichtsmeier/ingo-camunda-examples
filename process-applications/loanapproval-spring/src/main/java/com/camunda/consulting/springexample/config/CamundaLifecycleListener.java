package com.camunda.consulting.springexample.config;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication;
import org.springframework.context.ApplicationContext;

public class CamundaLifecycleListener implements ServletContextListener {
  
  private static final Logger log = Logger.getLogger(CamundaLifecycleListener.class.getName());
  
  public ApplicationContext ctx;
  private SpringServletProcessApplication springServletProcessApplication;

  public CamundaLifecycleListener(ApplicationContext ctx) {
    super();
    this.ctx = ctx;
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    log.info("contextDestroyed");
    springServletProcessApplication = (SpringServletProcessApplication) ctx.getBean("processApplication");
//    try {
//      springServletProcessApplication.annotatedUndeploy();
//    } catch (Exception e) {
//      // AHHHHHHHHHHHHHHHHHHHHHHH
//      e.printStackTrace();
//    }
    

  }

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    log.info("contextInitialized");

  }

}
