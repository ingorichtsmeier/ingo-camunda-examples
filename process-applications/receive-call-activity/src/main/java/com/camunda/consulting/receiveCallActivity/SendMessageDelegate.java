package com.camunda.consulting.receiveCallActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendMessageDelegate implements JavaDelegate {
  
  private static final Logger log = Logger.getLogger(SendMessageDelegate.class.getName());

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String documentId = (String) execution.getVariable("document");
    Map<String, Object> correlationKeys = new HashMap<String, Object>();
    correlationKeys.put("antragId", documentId);
    try {
      execution.getProcessEngineServices().getRuntimeService().correlateMessage("dokument_erhalten", correlationKeys);
    } catch (MismatchingMessageCorrelationException e) {
      log.severe(e.getMessage());
      throw new BpmnError("antrag_nicht_gefunden");
    }
  }

}
