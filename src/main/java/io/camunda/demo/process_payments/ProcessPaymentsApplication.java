package io.camunda.demo.process_payments;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Deployment(resources = {"process-payments.bpmn", "check-payment.form"})
public class ProcessPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessPaymentsApplication.class, args);
	}

}
