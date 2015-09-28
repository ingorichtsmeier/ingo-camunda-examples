package com.camunda.consulting.failing_job_example;

import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class RepairableServiceDelegate implements JavaDelegate {
  
  private static final Logger log = Logger.getLogger("org.camunda.RepairableServiceDelegate");

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    log.info("delegate started");
    log.info("wait 19 seconds");
    Thread.sleep(19000);
    log.info("service finished");
    String continue_ = (String) execution.getVariable("continue");
    if (continue_ != null && "continue".equals(continue_)) {
      log.info("delegate finished without error");
    } else {
      log.info("throw an exception as a failed job would do");
      throw new ProcessEngineException("Just for test purpose");
    }

  }

}
