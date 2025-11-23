package io.camunda.demo.process_payments.exception;

public class CreditCardExpiredException extends RuntimeException {
    public CreditCardExpiredException(String message) {
        super(message);
    }
}
