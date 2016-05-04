package com.camunda.consulting.training.repairCompensation.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.training.repairCompensation.data.Claim;
import com.camunda.consulting.training.repairCompensation.data.ClaimAction;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ActionBasedTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "action_checking";

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
  @Deployment(resources = "action-based-compensation.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = { "action-based-compensation.bpmn", "action-code-check.dmn" })
  public void testHappyPath() {
    ClaimAction action1 = new ClaimAction("act1", "10");
   
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, withVariables("action", action1));
    
    assertThat(processInstance).isActive();
    
    execute(job());
    
    assertThat(processInstance).isEnded();
    
	  ProcessTestCoverage.calculate(processInstance, rule.getProcessEngine());
  }
  
  @Deployment(resources = "IMEI-check.dmn")
  @Test
  public void testIMEICheck() {
    Claim claim1 = new Claim("claim1", "123456789012345678", null);
    DmnDecisionTableResult tableResult = processEngine().getDecisionService().evaluateDecisionTableByKey("IMEI-check", withVariables("claim", claim1));
    assertThat(tableResult.getFirstResult()).containsEntry("data-result", true);
  }

  @After
  public void calculateCoverageForAllTests() throws Exception {
    ProcessTestCoverage.calculate(rule.getProcessEngine());
  }  

}
