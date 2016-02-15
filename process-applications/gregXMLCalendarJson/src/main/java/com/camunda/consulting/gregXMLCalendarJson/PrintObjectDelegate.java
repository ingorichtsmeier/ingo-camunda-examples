package com.camunda.consulting.gregXMLCalendarJson;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.gregXMLCalendarJson.data.ExampleData;

@Named
public class PrintObjectDelegate implements JavaDelegate {
  
  private static Logger log = LoggerFactory.getLogger(PrintObjectDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    TypedValue variableTyped = execution.getVariableTyped("exampleData");
    ExampleData exampleData = (ExampleData) variableTyped.getValue();
    log.info("exampleData: {}", exampleData);
  }

}
