package com.camunda.consulting.training.repairCompensation.data;

/* The Payload to start a process instance by rest:
{
"variables": {
  "action": {
    "value": "{
      \"actionLineId\":\"15\", 
      \"code\":\"99\"
    }",
    "type": "Object",
    "valueInfo": {
      "serializationDataFormat": "application/json",
      "objectTypeName": "com.camunda.consulting.training.repairCompensation.data.ClaimAction"
    }
   }
  }
}
 */

import java.io.Serializable;

public class ClaimAction implements Serializable {
//  String repairServicePartner;
//  String productCodeIn;
//  String serialNumberIn;
//  String serialNumberOut;
//  String customerComplaintCode;
//  String problemFoundCode;
  String actionLineId;
  String code;
  public ClaimAction() {
  }
  public ClaimAction(String actionLineId, String actionCode) {
    super();
    this.actionLineId = actionLineId;
    this.code = actionCode;
  }
  public String getActionLineId() {
    return actionLineId;
  }
  public void setActionLineId(String actionLineId) {
    this.actionLineId = actionLineId;
  }
  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  
}
