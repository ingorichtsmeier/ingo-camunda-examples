package com.camunda.consulting.webservice;

import java.util.Date;

public class WebserviceExampleTask {
  
  String id;
  String name;
  String description;
  Date createTime;
  Date dueDate;
  Date followUpDate;
  String processInstanceId;
  
  public WebserviceExampleTask(String id, String name, String description, Date createTime, Date dueDate, Date followUpDate, String processInstanceId) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.createTime = createTime;
    this.dueDate = dueDate;
    this.followUpDate = followUpDate;
    this.processInstanceId = processInstanceId;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public Date getCreateTime() {
    return createTime;
  }
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
  public Date getDueDate() {
    return dueDate;
  }
  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }
  public Date getFollowUpDate() {
    return followUpDate;
  }
  public void setFollowUpDate(Date followUpDate) {
    this.followUpDate = followUpDate;
  }
  public String getProcessInstanceId() {
    return processInstanceId;
  }
  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  
}
