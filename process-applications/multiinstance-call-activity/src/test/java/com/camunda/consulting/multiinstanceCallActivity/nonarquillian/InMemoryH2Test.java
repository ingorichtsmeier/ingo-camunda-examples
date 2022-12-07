package com.camunda.consulting.multiinstanceCallActivity.nonarquillian;

import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@ExtendWith(ProcessEngineExtension.class)
public class InMemoryH2Test {

  private static final String PROCESS_DEFINITION_KEY = "muliinstance-call-activity";
  
  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = {"sequential/process.bpmn", "sequential/calledProcess.bpmn"})
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

}
