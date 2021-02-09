package io.assessment.banking.model.account.entity;

import java.time.ZonedDateTime;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.model.customer.entity.Customer;
import lombok.Data;

/**
 * Models the Account entity domain object
 *
 * @author Nikhil Vibhav
 */
@Data
@Entity(name = "account")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Enumerated
  @Column(nullable = false)
  private AccountType type;

  @Column(nullable = false)
  private Double balance = 0D;

  @ManyToOne
  @JoinColumn(nullable = false, updatable = false)
  private Customer customer;

  @Column(nullable = false)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private final ZonedDateTime dateCreated = ZonedDateTime.now();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private ZonedDateTime dateUpdated;
}
