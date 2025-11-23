package io.camunda.demo.process_payments.service;

import io.camunda.demo.process_payments.entity.Customer;
import io.camunda.demo.process_payments.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreditService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerCreditService.class);

    private final CustomerRepository customerRepository;

    public CustomerCreditService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Get the current customer credit from the database
     *
     * @return the current credit of the given customer
     */
    public Double getCustomerCredit(String customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    LOG.info("customer {} has credit of {}", customerId, customer.getCredit());
                    return customer.getCredit();
                })
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
    }

    public Double deductCredit(String customerId, Double amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        double openAmount;
        Double credit = customer.getCredit();
        Double deductedCredit;

        if (credit > amount) {
            deductedCredit = amount;
            openAmount = 0.0;
        } else {
            openAmount = amount - credit;
            deductedCredit = credit;
        }

        // update credit in DB
        customer.setCredit(credit - deductedCredit);
        customerRepository.save(customer);

        LOG.info("charged {} from the credit, open amount is {}", deductedCredit, openAmount);

        return openAmount;
    }
}

