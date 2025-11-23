package io.camunda.demo.process_payments.service;

import io.camunda.demo.process_payments.exception.CreditCardExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class CreditCardService {

    private static final Logger log = LoggerFactory.getLogger(CreditCardService.class);

    public void chargeAmount(String cardNumber, String cvc, String expirationDate, Double amount) {
        log.info("chargeAmount start");
        log.info(
                "charging card {} that expires on {} and has cvc {} with amount of {}",
                cardNumber,
                expirationDate,
                cvc,
                amount);

        if (!validateExpiryDate(expirationDate)) {
            log.error("Invalid expiry date");
            throw new CreditCardExpiredException("Invalid expiry date");
        }

        log.info("payment completed");
    }

    private boolean validateExpiryDate(String expiryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        try {
            YearMonth expiry = YearMonth.parse(expiryDate, formatter);
            YearMonth now = YearMonth.now();

            // Thẻ còn hạn nếu expiry >= tháng hiện tại
            return !expiry.isBefore(now);

        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
