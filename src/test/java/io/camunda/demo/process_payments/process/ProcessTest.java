package io.camunda.demo.process_payments.process;

import io.camunda.demo.process_payments.service.CreditCardService;
import io.camunda.demo.process_payments.service.CustomerCreditService;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTest;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static io.camunda.process.test.api.assertions.ElementSelectors.byName;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@CamundaProcessTest
@SpringBootTest
class ProcessTest {

    @Autowired
    ZeebeClient zeebeClient;
    @MockitoBean
    CustomerCreditService customerCreditService;
    @MockitoBean
    CreditCardService creditCardService;
    @Autowired
    private CamundaProcessTestContext camundaProcessTestContext;

    @BeforeEach
    void setUp() {
        CamundaAssert.setAssertionTimeout(Duration.ofMinutes(2));
        zeebeClient
                .newDeployResourceCommand()
                .addResourceFromClasspath("check-payment.form")
                .addResourceFromClasspath("payment_process.bpmn")
                .send()
                .join();
    }

    @Test
    void testHappyPath() throws InterruptedException, TimeoutException {
        given(customerCreditService.deductCredit(anyString(), anyDouble()))
                .willReturn(90.0);
        ProcessInstanceEvent processInstance =
                zeebeClient
                        .newCreateInstanceCommand()
                        .bpmnProcessId("paymentProcess")
                        .latestVersion()
                        .variables(
                                Map.of("customerId", "testCustomer", "orderTotal", 190.0, "expiryDate", "10/24"))
                        .send()
                        .join();

        CamundaAssert.assertThat(processInstance)
                .isCompleted()
                .hasCompletedElements(byName("Charge credit card"))
                .hasVariable("remainingAmount", 90.0);
        verify(customerCreditService).getCustomerCredit("testCustomer");
    }
}
