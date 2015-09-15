package com.camunda.consulting.jaxEndpointExample.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("resources")
public class HelloWorldApplication extends Application {
  
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(HelloWorldResource.class);
    return s;
  }

}
