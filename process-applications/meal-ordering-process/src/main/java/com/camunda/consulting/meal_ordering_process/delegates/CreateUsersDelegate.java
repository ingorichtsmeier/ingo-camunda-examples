package com.camunda.consulting.meal_ordering_process.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUsersDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(CreateUsersDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    LOG.info("create users");

  }

}
