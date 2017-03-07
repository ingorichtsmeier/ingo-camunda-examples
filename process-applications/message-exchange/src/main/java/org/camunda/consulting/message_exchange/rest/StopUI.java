package org.camunda.consulting.message_exchange.rest;

import java.net.URL;
import java.util.logging.Logger;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.json.JSONObject;

@Named
public class StopUI implements JavaDelegate {
	private final static Logger LOGGER = Logger.getLogger("LOAN-REQUESTS");

	@Override
	public void execute(DelegateExecution arg0) throws Exception {
		URL urltoread = new URL("http://127.0.0.1:8080/engine-rest/message");
		JSONObject processVariables = new JSONObject();
		JSONObject varJSON = new JSONObject();
		
		if((Boolean) arg0.getVariable("submit")) {
			varJSON = new JSONObject();
			varJSON.put("value",(String) arg0.getVariable("SendDATA"));
			varJSON.put("type", "String");
			
			processVariables.put("SendDATA",varJSON);
			
			Boolean var = (Boolean) arg0.getVariable("submit");
			varJSON = new JSONObject();
			varJSON.put("value",var);
			varJSON.put("type","Boolean");
			
			processVariables.put("submit",varJSON);
		} else {
			String[] varNames = {"NoAH", "salutation", "forename", "middlename", "surname", "NoLE", "DoB", "DoI", "ZIP", "city", "street", "streetNR", "nationality", "role", "CoR", "resStatus"};
			
			for(String varName: varNames) {
				String var = (String) arg0.getVariable(varName);
				varJSON = new JSONObject();
				varJSON.put("value",var);
				varJSON.put("type","String");
				
				processVariables.put(varName,varJSON);
			}
			
			varNames = new String[] {"corporate", "submit", "check"};
			
			for(String varName: varNames) {
				Boolean var = (Boolean) arg0.getVariable(varName);
				varJSON = new JSONObject();
				varJSON.put("value",var);
				varJSON.put("type","Boolean");
				
				processVariables.put(varName,varJSON);
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("messageName", "stop UI");
		json.put("processInstanceId" , (String) arg0.getVariable("backendId"));
		json.put("processVariables",processVariables);
		
		SendMessage.send(urltoread,json.toString(),false);
	}

}
