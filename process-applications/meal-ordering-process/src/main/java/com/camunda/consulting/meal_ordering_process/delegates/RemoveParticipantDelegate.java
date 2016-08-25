package com.camunda.consulting.meal_ordering_process.delegates;

import java.util.List;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.meal_ordering_process.data.Participant;

public class RemoveParticipantDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(RemoveParticipantDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    LOG.info("remove participant");
    String participantToRemove = (String) execution.getVariable("removeParticipant");
    RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
    List<Task> tasklist = execution.getProcessEngineServices().getTaskService().createTaskQuery().taskAssignee(participantToRemove).list();
    LOG.info("List size: " + tasklist.size());
    if (tasklist.size() > 0) {
      HistoricActivityInstance activityInstance = execution
          .getProcessEngineServices()
          .getHistoryService()
          .createHistoricActivityInstanceQuery()
          .executionId(tasklist.get(0).getExecutionId())
          .singleResult();
      LOG.info("remove activity instance " + activityInstance.getId());
      runtimeService
        .createProcessInstanceModification(execution.getProcessInstanceId())
        .cancelActivityInstance(activityInstance.getId())
        .execute();
    }
    
    List<Participant> participants = (List<Participant>) execution.getVariable("participants");
    for (int i = 0; i < participants.size(); i++) {
      if (participantToRemove.equals(participants.get(i).getEmail())) {
        participants.remove(i);
      }
    }
    execution.setVariable("participants", participants);
    
    //remove authorization and user
    AuthorizationService authorizationService = execution.getProcessEngineServices().getAuthorizationService();
    List<Authorization> authorizations = authorizationService.createAuthorizationQuery().userIdIn(participantToRemove).list();
    for (Authorization authorization : authorizations) {
      authorizationService.deleteAuthorization(authorization.getId());
    }
  }

}
