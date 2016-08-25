package com.camunda.consulting.meal_ordering_process.listener;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.meal_ordering_process.data.MealSelection;
import com.camunda.consulting.meal_ordering_process.data.Participant;

public class MealSelectionsTaskListener implements TaskListener {

  public static final Logger LOG = LoggerFactory.getLogger(MealSelectionsTaskListener.class);
  public static final String MEAL_SELECTIONS = "mealSelections";

  @Override
  public void notify(DelegateTask delegateTask) {
    String meal = (String) delegateTask.getExecution().getVariableLocal("meal");
    Participant participant = (Participant) delegateTask.getExecution().getVariableLocal("participant");
    MealSelection mealSelection = new MealSelection(participant.getName(), meal);
    LOG.info(mealSelection.toString());
    List<MealSelection> mealSelections = (List<MealSelection>) delegateTask.getExecution().getVariable(MEAL_SELECTIONS);
    if (mealSelections == null) {
      mealSelections = new ArrayList<MealSelection>();
    }
    
    mealSelections.add(mealSelection);
    
    delegateTask.setVariable(MEAL_SELECTIONS, mealSelections);

  }

}
