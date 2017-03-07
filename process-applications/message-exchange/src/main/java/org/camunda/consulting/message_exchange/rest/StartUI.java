package org.camunda.consulting.message_exchange.rest;

import java.net.URL;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class StartUI implements JavaDelegate {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(StartUI.class);
  
	@Override
	public void execute(DelegateExecution arg0) throws Exception {
		JSONObject varJSON = new JSONObject();
		JSONObject processVariables = new JSONObject();
		String var = null;
		Boolean bvar = false;

		String UI_ID = (String) arg0.getVariable("UI_ID");
		
		URL urltoread = new URL("http://127.0.0.1:8080/engine-rest/message");
		
		String[] varNames = {"NoAH", "salutation", "forename", "middlename", "surname", "NoLE", "DoB", "DoI", "ZIP", "city", "street", "streetNR", "nationality", "role", "CoR", "resStatus"};
		
		for(String varName: varNames) {
			try {
				var = (String) arg0.getVariable(varName);
			} catch (Exception e) {
				var = "";
			}
			varJSON = new JSONObject();
			varJSON.put("value",var);
			varJSON.put("type","String");
			
			processVariables.put(varName,varJSON);
			
			try {
				var = (String) arg0.getVariable(varName + "_r");
			} catch (Exception e) {
				var = "";
			}
			varJSON = new JSONObject();
			varJSON.put("value",var);
			varJSON.put("type","String");
			processVariables.put(varName + "_r",varJSON);
		}
		
		varNames = new String[] {"corporate", "submit", "check", "approved"};
		
		for(String varName: varNames) {
			try {
				bvar = (Boolean) arg0.getVariable(varName);
			} catch(Exception e) {
				bvar = false;
			}
			varJSON = new JSONObject();
			varJSON.put("value",bvar);
			varJSON.put("type","Boolean");
			
			processVariables.put(varName,varJSON);
		}
		
		varJSON = new JSONObject();
		varJSON.put("value", UI_ID);
		varJSON.put("type","String");
		
		JSONObject varJSON2 = new JSONObject();
		varJSON2.put("UI_ID", varJSON);
		
		JSONObject json = new JSONObject();
		json.put("messageName", "start UI");
		json.put("processInstanceId" , UI_ID);
		json.put("processVariables",processVariables);
		// json.put("correlationKeys", varJSON2); Test from CAMUNDA Support <-- doesn't work
		
		SendMessage.send(urltoread,json.toString(),false);
	}
}
