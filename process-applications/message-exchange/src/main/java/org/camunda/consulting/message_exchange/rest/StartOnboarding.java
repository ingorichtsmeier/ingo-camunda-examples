package org.camunda.consulting.message_exchange.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class StartOnboarding implements JavaDelegate {	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		RuntimeService runtimeService = BpmPlatform.getProcessEngineService().getDefaultProcessEngine().getRuntimeService();
		
		// Start Backend Execution and get Id
		map.put("UI_ID", execution.getProcessInstanceId());
		map.put("UI_Act_ID", execution.getProcessDefinitionId());
		
		String backendId = runtimeService.startProcessInstanceByMessage("start Onboarding", execution.getProcessBusinessKey(), map).getProcessInstanceId();
		
		// Write Variable to Execution
		execution.setVariable("backendId",backendId);
		execution.setVariable("UI_ID",  execution.getProcessInstanceId());
	}
}
