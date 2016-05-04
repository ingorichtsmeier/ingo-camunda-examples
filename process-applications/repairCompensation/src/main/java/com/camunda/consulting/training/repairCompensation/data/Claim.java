package com.camunda.consulting.training.repairCompensation.data;

import java.io.Serializable;
import java.util.List;

public class Claim implements Serializable {
  
  String claimId;
  String IMEI;
  List<ClaimAction> claimActions;
  public Claim(String claimId, String iMEI, List<ClaimAction> claimActions) {
    super();
    this.claimId = claimId;
    IMEI = iMEI;
    this.claimActions = claimActions;
  }
  public String getClaimId() {
    return claimId;
  }
  public void setClaimId(String claimId) {
    this.claimId = claimId;
  }
  public String getIMEI() {
    return IMEI;
  }
  public void setIMEI(String iMEI) {
    IMEI = iMEI;
  }
  public List<ClaimAction> getClaimActions() {
    return claimActions;
  }
  public void setClaimActions(List<ClaimAction> claimActions) {
    this.claimActions = claimActions;
  }
  
  

}
