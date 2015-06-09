package com.camunda.consulting;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.camunda.consulting.webservice.offertantrag.Offertantrag;

public class OfferteErstellenDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    Offertantrag offertantrag = (Offertantrag) execution.getVariable("offertantrag");
    System.out.println("" + offertantrag.getVersicherungsbeginn());
  }

}
