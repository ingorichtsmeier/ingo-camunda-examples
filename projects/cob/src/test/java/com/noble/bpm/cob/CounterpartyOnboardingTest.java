package com.noble.bpm.cob;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.List;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CounterpartyOnboardingTest {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	private static final String PROCESS_DEFINITION_KEY = "counterparty-onboarding";

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
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void testParsingAndDeployment() {
		// nothing is done here, as we just want to check for exceptions during deployment
	}
	
	@Test
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void overrideRequest() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
				withVariables(
						"CPTYNumber", new Long(567), 
						"gotoTax", "yes", 
						"gotoCompliance", "yes"));
		Task reviewOnboradingRequest = taskQuery().singleResult();
		complete(reviewOnboradingRequest);
		assertThat(pi).isWaitingAt("UserTask_1", "UserTask_3");

		ProcessInstance pi2 = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
				withVariables(
						"CPTYNumber", new Long(678), 
						"gotoTax", "yes", 
						"gotoCompliance", "yes"));
		reviewOnboradingRequest = taskQuery().processInstanceId(pi2.getProcessInstanceId()).singleResult();
		complete(reviewOnboradingRequest);
		assertThat(pi2).isWaitingAt("UserTask_1", "UserTask_3");

		List<Execution> overrideExecutions = runtimeService()
				.createExecutionQuery()
				.messageEventSubscriptionName("override")
				.processVariableValueEquals("CPTYNumber", new Long(567))
				.list();
		runtimeService().messageEventReceived(
				"override", 
				overrideExecutions.get(0).getId(), 
				withVariables("overrideReason", "I have to trade with them now"));
		assertThat(pi).isWaitingAt("UserTask_3", "UserTask_1", "Task_1");
		assertThat(pi2).isNotWaitingAt("Task_1");
	}
	
	@Test
	@Deployment(resources = "additional-approval.bpmn")
	public void predefinedApproval() {
		// start two process instances ... 
		ProcessInstance pi1 = runtimeService().startProcessInstanceByKey("additionalApproval", 
				withVariables("workId", 1));
		ProcessInstance pi2 = runtimeService().startProcessInstanceByKey("additionalApproval", 
				withVariables("workId", 2));
		// ... send just a message will fail ...
		try {
			runtimeService().correlateMessage("additionalApprovalMessage");
		} catch (MismatchingMessageCorrelationException e) {
			// ... because 2 instances are waiting for it.
			assertThat(e).hasMessageEndingWith("2 executions match the correlation keys. Should be one or zero.");
		}
		// Send a message to the process containing the variable workId=1 ...
		runtimeService().correlateMessage("additionalApprovalMessage", 
				withVariables("workId", 1));
		// ... will move the first process instance to the new approval task ... 
		assertThat(pi1).hasPassed("Task_1", "BoundaryEvent_1").isWaitingAt("UserTask_2");
		// ... and the other process instance is still in the first task.
		assertThat(pi2).isWaitingFor("additionalApprovalMessage").isWaitingAt("Task_1");
	}

}
