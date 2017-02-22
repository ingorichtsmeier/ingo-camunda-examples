package org.camunda.consulting.message_exchange.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {


@ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "user_interface";
  private static final String BUSINESS_PROCESS_BACKEND = "business_process_backend";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "messageing_issue.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "messageing_issue.bpmn")
  public void testHappyPath() {
	  ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, withVariables("UI_ID", "15"));
	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
	  assertThat(processInstance).isWaitingAt("start_UI").hasPassed("start_backend");
	  
	  ProcessInstance processInstanceBE = processInstanceQuery().processDefinitionKey(BUSINESS_PROCESS_BACKEND).singleResult();
	  assertThat(processInstanceBE).isWaitingAt("startEvent");
	  
	  Job jobForBackend = jobQuery().processDefinitionKey(BUSINESS_PROCESS_BACKEND).singleResult();
	  execute(jobForBackend);
	  assertThat(processInstanceBE).isWaitingAt("completed_UI");
  }

}
