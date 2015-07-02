package com.camunda.consulting.springexample.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class CamundaApplicationInitializer implements WebApplicationInitializer {

  public void onStartup(ServletContext servletContext) throws ServletException {
    try (AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext()) {
      ctx.register(CamundaConfig.class, CamundaSpringConfig.class);

      initContextParameters(servletContext, ctx);

      // add listener to the servlet context
      servletContext.addListener(new ContextLoaderListener(ctx));
      servletContext.addListener(new RequestContextListener());
      servletContext.addListener(new CamundaLifecycleListener(ctx)); // TODO: Hack - why does Spring
                                                                  // not shutdown correctly?
                                                                  // @PreDestroy not called

      // create a dispatcher servlet and add mapping to the root of the
      // context path
      DispatcherServlet dispatcherServlet = new DispatcherServlet(ctx);
      ServletRegistration.Dynamic dispatcher =
          servletContext.addServlet("Dispatcher", dispatcherServlet);
      dispatcher.setLoadOnStartup(1);
//      dispatcher.addMapping("/loanapproval");
      
      ctx.registerShutdownHook();
    }
  }

  private void initContextParameters(ServletContext servletContext,
      AnnotationConfigWebApplicationContext ctx) {
    // search for the configuration parameter to load the depending configuration file
    // src/main/webapp/WEB-INF/web.xml
    // contains context parameters: springprofiles, buildversion

    // /////////////////////////////////////////////////////////////////////////////////
    // build version

    // /////////////////////////////////////////////////////////////////////////////////
    // log levels

    // /////////////////////////////////////////////////////////////////////////////////
    // profile
//    String profilesParameter = servletContext.getInitParameter("springprofiles");
//    if (StringUtils.isEmpty(profilesParameter) || profilesParameter.equals(DEFAULT_SPRING_PARAM)) {
//      LOG.info("profiles parameter is configured to {}", profilesParameter);
//      profilesParameter = DEFAULT_SPRING_PARAM_VALUE;
//    }
//    LOG.info("set environment to {}", profilesParameter);
//    ctx.getEnvironment().setActiveProfiles(profilesParameter.split(","));
  }

}
