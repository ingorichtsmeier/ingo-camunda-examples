package org.camunda.consulting.message_exchange;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendUiMessageDelegate implements JavaDelegate {
	
	private static final Logger log = LoggerFactory.getLogger(SendUiMessageDelegate.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
		String messageName = "start UI";
		String uiId = (String) execution.getVariable("UI_ID");
		MessageCorrelationResult correlationResult = runtimeService
				.createMessageCorrelation(messageName)
				.processInstanceVariableEquals("UI_ID", uiId)
				.correlateWithResult();
		log.info("message sent and received: {}", correlationResult);
	}

}
