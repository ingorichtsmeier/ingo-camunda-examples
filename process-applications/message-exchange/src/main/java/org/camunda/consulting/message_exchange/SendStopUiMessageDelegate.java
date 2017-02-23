package org.camunda.consulting.message_exchange;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendStopUiMessageDelegate implements JavaDelegate {
	
	private static final Logger log = LoggerFactory.getLogger(SendStopUiMessageDelegate.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
		String messageName = "stop UI";
		String uiId = (String) execution.getVariable("UI_ID");
		Boolean approved = (Boolean) execution.getVariable("approved");
		MessageCorrelationResult correlationResult = runtimeService
				.createMessageCorrelation(messageName)
				.setVariable("approved", approved)
				.processInstanceVariableEquals("UI_ID", uiId)
				.correlateWithResult();
		log.info("message sent and received: {}", correlationResult);
	}

}
