package org.camunda.bpm.container.impl.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;

import org.camunda.bpm.container.ExecutorService;
import org.camunda.bpm.container.impl.threading.ra.outbound.JcaExecutorServiceConnection;
import org.camunda.bpm.container.impl.threading.ra.outbound.JcaExecutorServiceConnectionFactory;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;

@Stateless
@Local(ExecutorService.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExecutorServiceBean implements ExecutorService {

  @Resource(
//      name="java:app/eis/ingo/JcaExecutorServiceConnectionFactory", 
      mappedName="java:app/eis/embedded/JcaExecutorServiceConnectionFactory")
  protected JcaExecutorServiceConnectionFactory executorConnectionFactory;
  
  protected JcaExecutorServiceConnection executorConnection;

  @PostConstruct
  protected void openConnection() {
    try {
      executorConnection = executorConnectionFactory.getConnection();
    } catch (ResourceException e) {
      throw new ProcessEngineException("Could not open connection to executor service connection factory ", e);
    } 
  }
  
  @PreDestroy
  protected void closeConnection() {
    if(executorConnection != null) {
      executorConnection.closeConnection();
    }
  }

  @Override
  public boolean schedule(Runnable runnable, boolean isLongRunning) {
    return executorConnection.schedule(runnable, isLongRunning);
  }

  @Override
  public Runnable getExecuteJobsRunnable(List<String> jobIds, ProcessEngineImpl processEngine) {
    return executorConnection.getExecuteJobsRunnable(jobIds, processEngine);
  }
  
  public void dummyMethodTodestuingishTheClassFromTheShadedOne() {
    
  }

}
