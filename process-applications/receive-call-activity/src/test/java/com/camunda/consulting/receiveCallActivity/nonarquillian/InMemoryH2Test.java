package com.camunda.consulting.receiveCallActivity.nonarquillian;

import java.util.Arrays;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "diva_verarbeitung";

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
  @Deployment(resources = "NEO-DIVA_lesen.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }
  
  @Test
  @Deployment(resources = "NEO-DIVA_lesen.bpmn")
  public void testStartAndRecieveDocument() {
    ProcessInstance uw15 = runtimeService().startProcessInstanceByKey("underwriting", withVariables("antragId", "15"));
    ProcessInstance uw17 = runtimeService().startProcessInstanceByKey("underwriting", withVariables("antragId", "17"));
    
    assertThat(uw15).isWaitingAt("auf_dokument_warten");
    ProcessInstance dv15 = runtimeService().createProcessInstanceQuery().processDefinitionKey("dokumentenverarbeitung").variableValueEquals("antragId", "15").singleResult();
    assertThat(dv15).isWaitingAt("EventBasedGateway_1");
    
    runtimeService().startProcessInstanceByKey("diva_verarbeitung", withVariables("documentList", Arrays.asList("15", "17")));
    
    assertThat(uw15).isEnded();
    assertThat(uw17).isEnded();
  }
  
  @Test
  @Deployment(resources = "NEO-DIVA_lesen.bpmn")
  public void testAntragNichtGefunden() {
    ProcessInstance diva1517 = runtimeService().startProcessInstanceByKey("diva_verarbeitung", withVariables("documentList", Arrays.asList("15", "17")));
    
    assertThat(diva1517).hasNotPassed("scanstapel_updaten").hasPassed("dokumentenalter_pruefen").isEnded();
  }

}
