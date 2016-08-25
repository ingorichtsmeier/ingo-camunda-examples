package com.camunda.consulting.meal_ordering_process.listener;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.meal_ordering_process.data.Participant;

public class LoginStandardizationListener implements TaskListener {
  
  private static final Logger LOG = LoggerFactory.getLogger(LoginStandardizationListener.class);

  @Override
  public void notify(DelegateTask delegateTask) {
    LOG.info("Put emails to lowercase");
    List<Participant> participants = (List<Participant>) delegateTask.getExecution().getVariable("participants");
    for (Participant participant : participants) {
      participant.setEmail(participant.getEmail().toLowerCase());
    }
    delegateTask.getExecution().setVariable("participants", participants);
  }

}
