package com.camunda.consulting.webserviceExample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.io.File;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ArquillianTest {
  
  private static final String PROCESS_DEFINITION_KEY = "webservice-example";

  @Deployment
  public static WebArchive createDeployment() {
    // resolve given dependencies from Maven POM
    File[] libs = Maven.resolver()
      .offline(false)
      .loadPomFromFile("pom.xml")
      .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();

    return ShrinkWrap
            .create(WebArchive.class, "webservice-example.war")
            // add needed dependencies
            .addAsLibraries(libs)
            // prepare as process application archive for camunda BPM Platform
            .addAsResource("META-INF/processes.xml", "META-INF/processes.xml")
            // enable CDI
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            // boot JPA persistence unit
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            // add your own classes (could be done one by one as well)
            .addPackages(true, "com.camunda.consulting") // not recursive to skip package 'nonarquillian'
            // add process definition
            .addAsResource("Offerterstellung.bpmn")
    ;
  }

  @Inject
  private ProcessEngine processEngine;

  @Before
  public void setup() {
	init(processEngine);
  }

  /**
   * Tests that the process is executable and reaches its end.
   */
  @Test
  public void testProcessExecution() throws Exception {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("webservice-example");
    
    assertThat(pi).isWaitingAt("offertantrag_erfassen");
    
  }

}
