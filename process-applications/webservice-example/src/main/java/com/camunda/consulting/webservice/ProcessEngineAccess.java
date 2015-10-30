package com.camunda.consulting.webservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

@WebService
public class ProcessEngineAccess {
  
  @Inject
  ProcessEngine processEngine;
  
  @WebMethod
  public String startExampleProcessInstance(String name, Integer amount) {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("name", name);
    variables.put("amount", amount);
    ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("webservice-example", variables);
    return processInstance.getProcessInstanceId();
  }
  
  @WebMethod
  public List<WebserviceExampleTask> getAllTasksForWebserviceExample() {
    List<Task> taskList = processEngine.getTaskService().createTaskQuery().processDefinitionKey("webservice-example").list();
    List<WebserviceExampleTask> webserviceTaskList = new ArrayList<WebserviceExampleTask>(); 
    for (Task task : taskList) {
      webserviceTaskList.add(new WebserviceExampleTask(task.getId(), task.getName(), task.getDescription(), task.getCreateTime(), task.getDueDate(), task.getFollowUpDate(), task.getProcessInstanceId()));
    }
    return webserviceTaskList;
  }
  
  @WebMethod
  public void completeTask(String taskId, String newName, BigDecimal price) {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("name", newName);
    variables.put("price", price);
    processEngine.getTaskService().complete(taskId, variables);
  }
  
  @WebMethod
  public void correlateMessage(String messageName, String documentId) {
    Map<String, Object> correlationKeys = new HashMap<String, Object>();
    correlationKeys.put("documentId", documentId);
    processEngine.getRuntimeService().correlateMessage(messageName, correlationKeys);
  }

}
