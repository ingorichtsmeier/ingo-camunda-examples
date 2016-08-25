package com.camunda.consulting.meal_ordering_process.delegates;

import java.util.List;

import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.meal_ordering_process.data.Participant;

public class CleanUpUsersDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(CleanUpUsersDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    LOG.info("Clean up users");
    List<Participant> participants = (List<Participant>) execution.getVariable("participants");
    for (Participant participant : participants) {
      List<Authorization> authorizations = execution.getProcessEngineServices().getAuthorizationService().createAuthorizationQuery().userIdIn(participant.getEmail()).list();
      for (Authorization authorization : authorizations) {
        LOG.info("delete " + authorization.getResourceId());
        execution.getProcessEngineServices().getAuthorizationService().deleteAuthorization(authorization.getId());
      }
      execution.getProcessEngineServices().getIdentityService().deleteUser(participant.getEmail());
    }
  }

}
