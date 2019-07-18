package com.camunda.consulting.selfhealing_incident_handler_plugin;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.selfhealing_incident_handler_plugin.delegates.ResolveableTaskDelegate;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessJunitTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private static final String PROCESS_DEFINITION_KEY = "email-incident-handler-plugin";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
    Mocks.register("resolveableTask", new ResolveableTaskDelegate());
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() {
	  ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
        withVariables("shouldFail", true,
            "selfHealing", true));

	  assertThat(processInstance).isWaitingAt("raiseIncidentTask");
    try {
      execute(job());
    } catch (ProcessEngineException e) {
      e.printStackTrace();
    }
    execute(job());

    assertThat(processInstance).isWaitingAt("invokeSecondServiceTask").variables().contains(entry("shouldFail", false));
	  execute(job());

	  assertThat(processInstance).isEnded();
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testSelfHealingFailed() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, withVariables("shouldFail", true));

    assertThat(processInstance).isWaitingAt("raiseIncidentTask");
    try {
      execute(job());
    } catch (Exception e) {
      e.printStackTrace();
    }

    Incident incident = runtimeService().createIncidentQuery().singleResult();
    assertThat(incident.getIncidentMessage()).isEqualTo("my custom error");

    // simulate Cockpit operation
    runtimeService().setVariable(processInstance.getId(), "shouldFail", false);
    managementService().setJobRetries(job().getId(), 1);

    execute(job());

    assertThat(processInstance).isWaitingAt("invokeSecondServiceTask");
  }

}
