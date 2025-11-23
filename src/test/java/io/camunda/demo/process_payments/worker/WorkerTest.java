package io.camunda.demo.process_payments.worker;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import io.camunda.demo.process_payments.entity.Customer;
import io.camunda.demo.process_payments.repository.CustomerRepository;
import io.camunda.demo.process_payments.service.CustomerCreditService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WorkerTest {

    @Mock(stubOnly = true)
    ActivatedJob mockedJob;

    @Mock
    CustomerRepository customerRepository;

    CustomerCreditService customerCreditService;
    CustomerCreditWorker customerCreditHandler;

    @BeforeEach
    void setup() {
        // Inject the mocked repository into the service
        customerCreditService = new CustomerCreditService(customerRepository);
        customerCreditHandler = new CustomerCreditWorker(customerCreditService);
    }

    @Test
    public void testCustomerCreditWorker() {
        // Mock job variables
        given(mockedJob.getVariablesAsMap())
                .willReturn(Map.of("customerId", "khai202", "orderTotal", 20.0));

        // Mock repository behavior
        given(customerRepository.findById("testCustomer40"))
                .willReturn(Optional.of(new Customer("khai202", "Khai", 10.0, "Premium")));

        // Run the worker
        Map<String, Object> variables = customerCreditHandler.handle(mockedJob);

        // Verify result
        assertThat(variables).contains(entry("remainingAmount", 10.0));
    }

}