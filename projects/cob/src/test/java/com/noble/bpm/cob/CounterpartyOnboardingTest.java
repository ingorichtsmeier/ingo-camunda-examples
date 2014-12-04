package com.noble.bpm.cob;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
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
	public void testConditionsComplianceFineTaxMaybeNot() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewOnboardingRequest = taskQuery().singleResult();
		
		// business rules for compliance will be fine ... 
		complete(reviewOnboardingRequest, withVariables(
				"gotoTax", "yes", 
				"gotoCompliance", "no", 
				"complianceApproved", "yes"));
		
		// ... tax approval says maybe ...
		Task checkForTaxRules = taskQuery().singleResult();
		complete(checkForTaxRules, withVariables("taxApproved", "maybe"));
		
		// ... amend tax data ...
		assertThat(pi).isWaitingAt("UserTask_4");
		Task amendTask = taskQuery().singleResult();
		complete(amendTask);
		
		// ... amended data approval will fail ...
		assertThat(pi).isWaitingAt("UserTask_3");
		checkForTaxRules = taskQuery().singleResult();
		complete(checkForTaxRules, withVariables("taxApproved", "no"));
		
		// ... request has been rejected cause of failing tax approval
		assertThat(pi).isEnded().hasPassed("EndEvent_2");
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

	@Test
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void handleSubTask() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				withVariables("CPTYNumber", new Long(345)));
		Task reviewCounterpartyRequest = taskQuery().singleResult();
		
		// Create a new task with current task as parent
		Task newSubTask = taskService().newTask();
		TaskEntity newSubTaskEntity = (TaskEntity) newSubTask;
		newSubTaskEntity.setName("Additional review of counterparty");
		newSubTaskEntity.setParentTaskId(reviewCounterpartyRequest.getId());
		newSubTaskEntity.setDescription("This is an example for a subtask");
		newSubTaskEntity.setProcessInstanceId(reviewCounterpartyRequest.getProcessInstanceId());
		taskService().saveTask(newSubTaskEntity);
		
		// put it into a group list
		taskService().addCandidateGroup(newSubTask.getId(), "management");

		// give him a from key as a variable
		taskService().setVariable(newSubTask.getId(), "my form key", "helloWorldForm");

		newSubTask = taskQuery().taskId(newSubTask.getId()).singleResult();
		assertThat(newSubTask.getProcessInstanceId()).isEqualTo(pi.getId());

		// access process variable with the process instance id 
		Map<String, Object> variables = runtimeService().getVariables(newSubTask.getProcessInstanceId());
		assertThat(variables).containsEntry("CPTYNumber", new Long(345));

		Map<String, Object> subTaskVars = taskService().getVariables(newSubTask.getId());
		assertThat(subTaskVars).containsEntry("my form key", "helloWorldForm");
		
		// work on the subtask
		newSubTask = taskQuery().taskName("Additional review of counterparty").singleResult();
		taskService().claim(newSubTask.getId(), "peter");
		
		// return the variables directly to the process instance
		Map<String, Object> taskResults = withVariables("additionalApproval", "yes");
		runtimeService().setVariables(newSubTask.getProcessInstanceId(), taskResults);
		taskService().complete(newSubTask.getId());

		Map<String, Object> procVars = runtimeService().getVariables(pi.getId());
		assertThat(procVars).containsEntry("additionalApproval", "yes");

		// the task appears in the history of the process instance
		List<HistoricTaskInstance> passedTask = historyService().createHistoricTaskInstanceQuery().processInstanceId(pi.getId()).list();
		assertThat(passedTask).extracting("name").contains("Additional review of counterparty", "Review and complete onboarding request");
		
		// work on the origin task
		taskService().complete(reviewCounterpartyRequest.getId(), 
				withVariables(
						"gotoTax", "no", 
						"gotoCompliance", "no", 
						"taxApproved", "yes",
						"complianceApproved", "yes"));
		
		assertThat(pi).isEnded();
	}
	
	@Test
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void testDueDate() {
		runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewRequest = taskQuery().singleResult();
		System.out.println("Due date:" + reviewRequest.getDueDate());
		assertThat(reviewRequest.getDueDate()).isAfter(new Date(System.currentTimeMillis() + 7000000L))
			.isBefore(new Date(System.currentTimeMillis() + 75000000L));
	}
	
}
