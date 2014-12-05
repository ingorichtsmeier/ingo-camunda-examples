package com.noble.bpm.cob.subprocess;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CancelProcessDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String subProcessInstanceId = (String) execution.getVariable("ssiApprovalInstanceId");
		RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
		runtimeService.setVariable(subProcessInstanceId, "cancelReason", "original counterparty request is rejected");
		runtimeService.deleteProcessInstance(subProcessInstanceId, "original counterparty request is rejected");
	}

}
