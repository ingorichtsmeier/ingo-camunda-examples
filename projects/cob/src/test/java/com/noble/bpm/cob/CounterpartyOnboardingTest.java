package com.noble.bpm.cob;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.impl.util.LogUtil.ThreadLogMode;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
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
	@Deployment(resources = { "counterparty-onboarding.bpmn", "ssi-approval.bpmn" })
	public void testParsingAndDeployment() {
		// nothing is done here, as we just want to check for exceptions during deployment
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"}) 
	public void testConditionsComplianceFineTaxMaybeNot() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewOnboardingRequest = taskQuery().singleResult();
		
		// business rules for compliance will be fine ... 
		complete(reviewOnboardingRequest, withVariables(
				"gotoTax", "yes", 
				"gotoCompliance", "no", 
				"complianceApproved", "yes"));
		
		// ... tax approval says maybe ...
		Task checkForTaxRules = taskQuery().processInstanceId(pi.getId()).singleResult();
		complete(checkForTaxRules, withVariables("taxApproved", "maybe"));
		
		// ... amend tax data ...
		assertThat(pi).isWaitingAt("UserTask_4");
		Task amendTask = taskQuery().processInstanceId(pi.getId()).singleResult();
		complete(amendTask);
		
		// ... amended data approval will fail ...
		assertThat(pi).isWaitingAt("UserTask_3");
		checkForTaxRules = taskQuery().processInstanceId(pi.getId()).singleResult();
		complete(checkForTaxRules, withVariables("taxApproved", "no"));
		
		// complete the async service task
		Job continuation = jobQuery().messages().singleResult();
		execute(continuation);
		
		// ... request has been rejected cause of failing tax approval
		assertThat(pi).isEnded().hasPassed("EndEvent_2");		
	}

	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
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
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
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
		List<HistoricTaskInstance> passedTasks = historyService().createHistoricTaskInstanceQuery().processInstanceId(pi.getId()).list();
		assertThat(passedTasks).extracting("name").contains("Additional review of counterparty", "Review and complete onboarding request");
		
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
		// due date is set to PT2H, two hours
		runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewRequest = taskQuery().singleResult();
		assertThat(reviewRequest.getDueDate()).isAfter(new Date(System.currentTimeMillis() + 295 * 60 * 1000L))
			.isBefore(new Date(System.currentTimeMillis() + 305 * 60 * 1000L));
	}
	
	@Test
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void testEscalationJobCreated() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		List<Job> jobList = jobQuery().list();
		assertThat(jobList).hasSize(1);
		Job job = jobList.get(0);
		String deploymentId = ((ExecutionEntity)pi).getProcessDefinition().getDeploymentId();
		assertThat(job)
			.hasProcessInstanceId(pi.getId())
			.hasDeploymentId(deploymentId)
			.hasExecutionId(taskQuery().singleResult().getExecutionId());
		assertThat(job.getProcessDefinitionId()).isEqualTo(pi.getProcessDefinitionId());
		assertThat(job.getProcessDefinitionKey()).isEqualTo(PROCESS_DEFINITION_KEY);
		// handlerCfg can transport some data
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
	public void deleteTimerWithCompletion() {
		runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewRequest = taskQuery().singleResult();
		complete(reviewRequest, withVariables("gotoTax", "yes", "gotoCompliance", "no"));
		List<Job> jobList = jobQuery().list();
		assertThat(jobList).hasSize(1); // the timer for compliance
		TimerEntity timer = (TimerEntity) jobList.get(0);
		assertThat(timer.getJobHandlerType()).isEqualTo(EscalationJobHandler.ESCALATION_JOB_HANDLER_TYPE);
	}
	
	@Test
	@Deployment(resources = "counterparty-onboarding.bpmn")
	public void testTaskEscalated() throws InterruptedException {
		// Prepare the test engine 
		EscalationJobHandler mockedHandler =  mock(EscalationJobHandler.class);
		// show threads on the console output
		LogUtil.setThreadLogMode(ThreadLogMode.INDENT);
		// register the jobHandler in the engine
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) rule.getProcessEngine().getProcessEngineConfiguration();
		Map<String, JobHandler> jobHandlers = processEngineConfiguration.getJobHandlers();
		jobHandlers.put(EscalationJobHandler.ESCALATION_JOB_HANDLER_TYPE, mockedHandler);
		processEngineConfiguration.setJobHandlers(jobHandlers);
		
		// start the process instance
		runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		// move 2 hours and 5 minutes ahead
		ClockUtil.setCurrentTime(new Date(System.currentTimeMillis() + 75000000L));
		
		// start the job executor on the test engine 
		processEngineConfiguration.getJobExecutor().start();
		Thread.sleep(1000L);
		
		// check if the jobHandler was called
		verify(mockedHandler).execute(anyString(), any(ExecutionEntity.class), any(CommandContext.class));
		processEngineConfiguration.getJobExecutor().shutdown();
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn" })
	public void startSsiProcess() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
				withVariables("CPTYNumber", new Long(345)));
		Task reviewRequest = taskQuery().singleResult();
		complete(reviewRequest, withVariables(
				"gotoTax", "yes", 
				"gotoCompliance", "no"));
		List<ProcessInstance> procInsts = runtimeService().createProcessInstanceQuery().list();
		assertThat(procInsts).hasSize(2);
		assertThat(pi).hasVariables("ssiApprovalInstanceId");
		String ssiApprovalProcessInstanceId = (String) runtimeService().getVariable(pi.getId(), "ssiApprovalInstanceId");
		ProcessInstance ssiApproval = runtimeService().createProcessInstanceQuery().processInstanceId(ssiApprovalProcessInstanceId).singleResult();
		assertThat(ssiApproval).hasVariables("CPTYNumber");
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
	public void cancelSsiProcess() {
		runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
		Task reviewRequest = taskQuery().singleResult();
		complete(reviewRequest, withVariables(
				"gotoTax", "no",
				"taxApproved", "no", 
				"gotoCompliance", "no",
				"complianceApproved", "no"));
		Job continuation = jobQuery().messages().singleResult();
		execute(continuation);
		List<ProcessInstance> procInsts = runtimeService().createProcessInstanceQuery().list();
		assertThat(procInsts).isEmpty();
		assertThat(historyService()
				.createHistoricVariableInstanceQuery()
				.variableName("ssiApprovalInstanceId")
				.singleResult()).isNotNull();
		assertThat(historyService()
				.createHistoricVariableInstanceQuery()
				.variableName("cancelReason")
				.singleResult().getValue()).isEqualTo("original counterparty request is rejected");
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
	public void completeSsiProcess() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, 
				withVariables("CPTYNumber", new Long(345)));
		Task reviewRequest = taskQuery().singleResult();
		complete(reviewRequest, withVariables(
				"gotoTax", "yes", 
				"gotoCompliance", "no",
				"complianceApproved", "no"));
		// ssi process started
		ProcessInstance ssi = runtimeService().createProcessInstanceQuery().processDefinitionKey("ssi-approval").singleResult();
		Task approveSsi = taskQuery().processDefinitionKey("ssi-approval").singleResult();
		complete(approveSsi, withVariables("ssiApproved", "yes"));
		assertThat(ssi).isEnded().hasPassed("EndEvent_1");
		
		// contine the counterparty process
		Task checkForTaxRules = taskQuery().singleResult();
		complete(checkForTaxRules, withVariables("taxApproved", "no"));
		
		Job continuation = jobQuery().singleResult();
		execute(continuation);
		assertThat(pi).isEnded();
	}
	
	@Test
	@Deployment(resources = {"counterparty-onboarding.bpmn", "ssi-approval.bpmn"})
	public void callHighRiskCountryCheck() {
		ProcessInstance pi = runtimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, withVariables(
				"CPTYNumber", new Long(789), 
				"CPTYName", "aCounterParty", 
				"country", "Nigeria"));
		Task reviewRequest = taskQuery().singleResult();
		complete(reviewRequest, withVariables("gotoTax", "yes", "gotoCompliance", "no"));
		// user task check for compliance rules
		assertThat(pi).hasVariables("isHighRiskCountry");
		String highRiskCountry = (String) runtimeService().getVariable(pi.getId(), "isHighRiskCountry");
		assertThat(highRiskCountry).isEqualTo("yes");
	}

}
