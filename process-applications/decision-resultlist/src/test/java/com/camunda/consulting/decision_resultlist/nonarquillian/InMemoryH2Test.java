package com.camunda.consulting.decision_resultlist.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "decision-resultlist";

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
  @Deployment(resources = {"process.bpmn", "example-decision.dmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = {"process.bpmn", "example-decision.dmn"})
  public void testHappyPath() {
    List<Integer> inputList = Arrays.asList(new Integer[] {1, 3, 2});
    ArrayList<Integer> resultList = new ArrayList<Integer>();
        
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
        withVariables("inputList", inputList,
            "resultList", resultList));
	  
	  assertThat(processInstance).isWaitingAt("EndEventProcessEnded");
	  
	  assertThat(processInstance).hasVariables("resultList");
	  
	  List<Integer> resultListAfter = (List<Integer>) runtimeService().getVariable(processInstance.getId(), "resultList");
	  
	  assertThat(resultListAfter).containsExactly(10, 30, 20);
  }

  @After
  public void calculateCoverageForAllTests() throws Exception {
    ProcessTestCoverage.calculate(rule.getProcessEngine());
  }  

}
