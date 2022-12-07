package com.camunda.consulting.multiinstanceCallActivity;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class CamundaProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(CamundaProcessApplication.class, args);
  }

}
