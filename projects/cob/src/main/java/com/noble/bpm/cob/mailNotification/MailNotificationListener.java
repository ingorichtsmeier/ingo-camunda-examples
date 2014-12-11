package com.noble.bpm.cob.mailNotification;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import lombok.extern.java.Log;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.task.IdentityLink;

public class MailNotificationListener implements TaskListener {
	
	private static final Logger log = Logger.getLogger(MailNotificationListener.class.getName());

	@Override
	public void notify(DelegateTask delegateTask) {
		Set<IdentityLink> candidates = delegateTask.getCandidates();
		// goto Active directory and look for all the members
		
		for (IdentityLink identityLink : candidates) {
			// get the member of candidates
			String groupId = identityLink.getGroupId();
			List<User> usersToNotify = delegateTask.getProcessEngineServices().getIdentityService().createUserQuery().memberOfGroup(groupId).list();
			sendNotificationEmail(usersToNotify, delegateTask);
		}
	}

	public void sendNotificationEmail(List<User> usersToNotify, DelegateTask delegateTask) {
		
		ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();

		Email email = new SimpleEmail();
		email.setCharset("utf-8");
		email.setHostName(processEngineConfiguration.getMailServerHost());
		email.setAuthentication(processEngineConfiguration.getMailServerUsername(), processEngineConfiguration.getMailServerPassword());
		try {
			email.setFrom("noreply@camunda.org");
			email.setSubject("Task assigned: " + delegateTask.getName());
			email.setMsg("Please complete: " + 
					delegateTask.getName() + 
					" at  http://localhost:8080/camunda/app/tasklist/default/#/task/" + 
					delegateTask.getId());
			for (User user : usersToNotify) {
				String completeName = user.getFirstName() + " " + user.getLastName();
				String recipient = user.getEmail();
				email.addTo(recipient, completeName, "utf-8");
				log.info("added " + recipient + ", " + completeName + " to the list of recients of task " + delegateTask.getId());
			}
			email.send();
		} catch (EmailException e) {
			log.warning(e.getMessage());
		}
	}

}