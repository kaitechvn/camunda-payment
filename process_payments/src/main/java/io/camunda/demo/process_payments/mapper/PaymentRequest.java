package io.camunda.demo.process_payments.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String cardNumber;
    private String cvc;
    private String expirationDate;
    private Double amount;
}
