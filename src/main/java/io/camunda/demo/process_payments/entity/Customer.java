package io.camunda.demo.process_payments.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Customer {

    @Id
    private String id;

    private String name;

    private Double credit;

    private String level;
}
