package com.camunda.consulting.webserviceExample.nonarquillian;

import java.util.Date;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.webservice.offertantrag.Basispaket;
import com.camunda.consulting.webservice.offertantrag.Offertantrag;
import com.camunda.consulting.webservice.offertantrag.VersicherungsdauerEnum;
import com.camunda.consulting.webservice.offertantrag.VersicherungsdeckungEnum;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "webservice-example";

  // enable more detailed logging
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath(); // process engine
    LogFactory.useJdkLogging(); // MyBatis
  }
  
  @Before
  public void setup() {
	init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = "Offerterstellung.bpmn")
  public void testStartProcessInstance() {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey("offerterstellung");
    
    assertThat(pi).isWaitingAt("offertantrag_erfassen");
    
    Offertantrag offertantrag = new Offertantrag(VersicherungsdeckungEnum.Einzelperson, new Date(), VersicherungsdauerEnum.Kurzfristversicherung31Tage, new Basispaket());
    Task offertantragErfassen = taskQuery().singleResult();
    complete(offertantragErfassen, withVariables("offertantrag", offertantrag));
    
    
  }

}
