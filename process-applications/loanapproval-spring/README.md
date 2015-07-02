Process Application with java-based configured container
========================================================

This example shows how to configure the container for the process application in Java using `@Configure` and `@Bean`.
It uses the sample application from the spring getting started guide.

The process instance is started after deployment.

![process definition](src/main/resources/loanApproval.png)

Drawbacks
---------

How to unregister the process application for redeployment?

In the moment you get an exception when you deploy a new version of the war-file:

    SCHWERWIEGEND: Exception sending context initialized event to listener instance of class org.springframework.web.context.ContextLoaderListener
    org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'processApplication' defined in class com.camunda.consulting.springexample.config.CamundaConfig: Invocation of init method failed; nested exception is org.camunda.bpm.engine.ProcessEngineException: Exception while performing 'Deployment of Process Application MyProcessApplication => Start Process Application Service': Cannot register service org.camunda.bpm.platform.job-executor.process-application:type=MyProcessApplication with MBeans Container, service with same name already registered.

catalina-log shows, that the old application is called to be destroyed:

    Jul 01, 2015 10:07:04 PM org.apache.catalina.startup.HostConfig undeploy
    INFORMATION: Undeploying context [/loanapproval-spring-0.0.1-SNAPSHOT]
    Jul 01, 2015 10:07:04 PM com.camunda.consulting.springexample.config.CamundaLifecycleListener contextDestroyed
    INFORMATION: contextDestroyed
    Jul 01, 2015 10:07:04 PM org.apache.catalina.startup.HostConfig deployWAR
    INFORMATION: Deploying web application archive C:\Arbeit\camunda\camunda-bpm-ee-tomcat-7.3.1-ee\server\apache-tomcat-7.0.62\webapps\loanapproval-spring-0.0.1-SNAPSHOT.war

but how? 

Useful links
------------
[http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-java](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-java)
    