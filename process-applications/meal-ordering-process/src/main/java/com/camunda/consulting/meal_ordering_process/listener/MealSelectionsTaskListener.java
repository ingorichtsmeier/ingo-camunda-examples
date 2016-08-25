package com.camunda.consulting.meal_ordering_process.listener;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import com.camunda.consulting.meal_ordering_process.data.MealSelection;

public class MealSelectionsTaskListener implements TaskListener {

  public static final String MEAL_SELECTIONS = "mealSelections";

  @Override
  public void notify(DelegateTask delegateTask) {
    MealSelection meal = (MealSelection) delegateTask.getExecution().getVariableLocal("meal");
    List<MealSelection> mealSelections = (List<MealSelection>) delegateTask.getExecution().getVariable(MEAL_SELECTIONS);
    if (mealSelections == null) {
      mealSelections = new ArrayList<MealSelection>();
    }
    
    mealSelections.add(meal);
    
    delegateTask.setVariable(MEAL_SELECTIONS, mealSelections);

  }

}
