package com.camunda.consulting.selfhealing_generic_process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfhealingDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(SelfhealingDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    LOG.info("Do the self healing");

  }

}
