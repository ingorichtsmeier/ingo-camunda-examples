package com.camunda.consulting.selfhealing_incident_handler_plugin;

import org.camunda.bpm.engine.impl.incident.DefaultIncidentHandler;
import org.camunda.bpm.engine.impl.incident.IncidentContext;
import org.camunda.bpm.engine.impl.persistence.entity.IncidentEntity;
import org.camunda.bpm.engine.runtime.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfHealingIncidentHandler extends DefaultIncidentHandler {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(SelfHealingIncidentHandler.class);

  public SelfHealingIncidentHandler(String type) {
    super(type);
    LOGGER.info("SelfHealingIncidentHandler created for type '{}'", type);
  }

  @Override
  public String getIncidentHandlerType() {
    LOGGER.info("get handler type '{}'", type);
    return super.getIncidentHandlerType();
  }

  @Override
  public Incident handleIncident(IncidentContext context, String message) {
    IncidentEntity incidentEntity = (IncidentEntity) super.handleIncident(context, message);
    LOGGER.info("incident {} created for execution {}", incidentEntity.getId(), context.getExecutionId());
    
    LOGGER.info("invoke self healing in the engine");
    Boolean successful = invokeSelfHealing(incidentEntity);
    if (successful) {
      LOGGER.info("Resolving incident");
      resolveIncident(context);
    }
    
    return incidentEntity;
  }

  @Override
  public void resolveIncident(IncidentContext context) {
    LOGGER.info("resolve incident for execution {}", context.getExecutionId());
    LOGGER.info("context configuration: {}", context.getConfiguration());
    super.resolveIncident(context);
  }

  @Override
  public void deleteIncident(IncidentContext context) {
    LOGGER.info("delete incident for execution {}", context.getExecutionId());
    super.deleteIncident(context);
    
  }

  public Boolean invokeSelfHealing(IncidentEntity incidentEntity) {
    LOGGER.info("Do the magic self healing");
    Boolean successful = (Boolean) incidentEntity.getExecution().getVariable("selfHealing");
    if (successful != null && successful) {
      incidentEntity.getExecution().setVariable("shouldFail", false);
      return true;
    }
    else {
      LOGGER.info("SelfHealing failed");
      return false;
    }
  }
  
}
