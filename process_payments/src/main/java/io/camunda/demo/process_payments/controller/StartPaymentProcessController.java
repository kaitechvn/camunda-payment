package io.camunda.demo.process_payments.controller;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/process/payments")
public class StartPaymentProcessController {

    private static final Logger logger = LoggerFactory.getLogger(StartPaymentProcessController.class);

    @Autowired private ZeebeClient zeebeClient;

    @PostMapping("/start")
    public void startProcess(@RequestBody Map<String, Object> variables) {

        logger.info("Start process");

        zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("paymentProcess")
                .latestVersion()
                .variables(variables)
                .send()
                .join();
    }
}
