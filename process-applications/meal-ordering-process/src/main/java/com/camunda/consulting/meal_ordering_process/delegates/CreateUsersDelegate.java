package com.camunda.consulting.meal_ordering_process.delegates;

import java.util.List;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.consulting.meal_ordering_process.data.Participant;

public class CreateUsersDelegate implements JavaDelegate {
  
  private static final Logger LOG = LoggerFactory.getLogger(CreateUsersDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    LOG.info("Create users");
    List<Participant> participants = (List<Participant>) execution.getVariable("participants");
    AuthorizationService authorizationService = execution.getProcessEngineServices().getAuthorizationService();

    for (Participant participant : participants) {
      UserEntity user = new UserEntity(participant.getEmail());
      user.setPassword("ca051");
      user.setFirstName("");
      user.setLastName(participant.getName());
      execution.getProcessEngineServices().getIdentityService().saveUser(user);
    
      Authorization taskListAccess = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
      taskListAccess.setUserId(participant.getEmail());
      taskListAccess.setResource(Resources.APPLICATION);
      taskListAccess.setResourceId("tasklist");
      taskListAccess.addPermission(Permissions.ACCESS);
      authorizationService.saveAuthorization(taskListAccess);
      
      Authorization processInstanceWork = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
      processInstanceWork.setUserId(participant.getEmail());
      processInstanceWork.setResource(Resources.PROCESS_DEFINITION);
      processInstanceWork.setResourceId("MealOrderingProcess");
      processInstanceWork.addPermission(Permissions.UPDATE_INSTANCE);
      processInstanceWork.addPermission(Permissions.READ);
      authorizationService.saveAuthorization(processInstanceWork);
    }
    
  }

}
