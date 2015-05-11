package com.camunda.consulting.multiinstanceCallActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class ListHandlerBean {
  
  private static final Logger log = Logger.getLogger(ListHandlerBean.class.getName());
  
  public List<String> addElementToList(List<String> outputList, String element) {
    outputList.add(element);
    log.info("outputList : " + outputList);
    return outputList;
  }
  
  public void saveEmptyListInProcessInstance(DelegateExecution execution) {
    execution.getProcessEngineServices().getRuntimeService().setVariable(execution.getProcessInstanceId(), "outputList", new ArrayList<String>());
  }
  
  public void showList(List<String> list) {
    log.info("this is the list: " + list);
  }
  
  public void saveVariable(String varName, Object varValue, DelegateExecution execution) {
    execution.setVariable(varName, varValue);
  }

}
