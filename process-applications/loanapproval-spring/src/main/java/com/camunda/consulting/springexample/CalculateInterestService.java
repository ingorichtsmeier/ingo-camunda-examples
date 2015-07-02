package com.camunda.consulting.springexample;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CalculateInterestService implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("Spring Bean invoked.");
    System.out.println("next line");
  }

}
