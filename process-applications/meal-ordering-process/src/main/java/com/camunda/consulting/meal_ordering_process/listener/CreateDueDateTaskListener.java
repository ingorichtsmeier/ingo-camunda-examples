package com.camunda.consulting.meal_ordering_process.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import com.camunda.consulting.meal_ordering_process.data.Training;

public class CreateDueDateTaskListener implements TaskListener {

  @Override
  public void notify(DelegateTask delegateTask) {
    Training training = (Training) delegateTask.getExecution().getVariable("training");
    Date dueDate = training.getStartDate();
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(dueDate);
    calendar.set(Calendar.HOUR_OF_DAY, 11);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    delegateTask.setDueDate(calendar.getTime());

  }

}
