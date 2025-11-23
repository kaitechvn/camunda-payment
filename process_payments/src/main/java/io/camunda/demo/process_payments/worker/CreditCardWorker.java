package io.camunda.demo.process_payments.worker;

import io.camunda.demo.process_payments.exception.CreditCardExpiredException;
import io.camunda.demo.process_payments.service.CreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreditCardWorker {

    private static final Logger log = LoggerFactory.getLogger(CreditCardWorker.class.getName());

    private final CreditCardService creditCardService;

    @JobWorker(type = "creditCardCharging")
    public void handle(JobClient client, ActivatedJob job) {
        log.info("Handling credit card payment for process instance {}", job.getProcessInstanceKey());
        Map<String, Object> variables = job.getVariablesAsMap();
        String cardNumber = (String) variables.get("cardNumber");
        String cvc = (String) variables.get("cvc");
        String expiryDate = (String) variables.get("expiryDate");
        Double amount = Double.valueOf(variables.get("openAmount").toString());
        try {
            creditCardService.chargeAmount(cardNumber, cvc, expiryDate, amount);
            client.newCompleteCommand(job).send();
        } catch (CreditCardExpiredException e) {
            client
                    .newThrowErrorCommand(job)
                    .errorCode("creditCardErrorCode")
                    .errorMessage(e.getLocalizedMessage())
                    .variables(Map.of("errorMessage", e.getLocalizedMessage()))
                    .send();
        }
    }
}
