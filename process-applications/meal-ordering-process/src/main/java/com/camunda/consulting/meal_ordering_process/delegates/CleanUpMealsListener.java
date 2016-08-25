package com.camunda.consulting.meal_ordering_process.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class CleanUpMealsListener implements ExecutionListener {

  @Override
  public void notify(DelegateExecution execution) throws Exception {
    execution.removeVariable("mealSelections");
  }

}
