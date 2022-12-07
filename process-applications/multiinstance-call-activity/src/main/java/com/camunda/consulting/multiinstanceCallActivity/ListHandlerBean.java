package com.camunda.consulting.multiinstanceCallActivity;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ListHandlerBean {
  
  private static final Logger log = LoggerFactory.getLogger(ListHandlerBean.class);
  
  public List<String> addElementToList(List<String> outputList, String element) {
    outputList.add(element);
    log.info("outputList : " + outputList);
    return outputList;
  }
  
  public List<String> createEmptyList() {
    return new ArrayList<String>();
  }
  
  public void showList(String process, List<String> list) {
    log.info(process + ": this is the list: " + list);
  }
  
  public void saveVariable(String varName, Object varValue, DelegateExecution execution) {
    execution.setVariable(varName, varValue);
  }
  
  public void showValue(String value) {
    log.info("Current value: {}", value);
  }

}
