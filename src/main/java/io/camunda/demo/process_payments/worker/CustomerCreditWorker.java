package io.camunda.demo.process_payments.worker;

import io.camunda.demo.process_payments.service.CreditCardService;
import io.camunda.demo.process_payments.service.CustomerCreditService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomerCreditWorker {

    private static final Logger log = LoggerFactory.getLogger(CustomerCreditWorker.class.getName());

    private final CustomerCreditService customerCreditService;

    @JobWorker(type = "customerCreditHandling")
    public Map<String, Object> handle (ActivatedJob job) {
        log.info("Handling customer credit for process instance {}", job.getProcessInstanceKey());

        Map<String, Object> variables = job.getVariablesAsMap();
        String customerId = (String) variables.get("customerId");
        Double amount = Double.valueOf(variables.get("orderTotal").toString());

        Double customerCredit = customerCreditService.getCustomerCredit(customerId);
        Double remainingAmount = customerCreditService.deductCredit(customerId, amount);

        return Map.of("customerCredit", customerCredit, "remainingAmount", remainingAmount);

    }

}
