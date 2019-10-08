package com.camunda.consulting.selfhealing_generic_process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInvocationDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(ServiceInvocationDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String superProcessId = execution.getSuperExecution().getId();
    LOG.info("Super Process ID: {}", superProcessId);
    Order order = (Order) execution.getSuperExecution().getVariable("order");
    LOG.info("Customer Name: {}", order.getCustomerName());
    
    order.setFirstAddition("My first addition");
    execution.getSuperExecution().setVariable("order", order);
    
    Boolean shouldComplete = (Boolean) execution.getVariable("shouldComplete");
    execution.setVariable("service_completed", shouldComplete);
  }

}
