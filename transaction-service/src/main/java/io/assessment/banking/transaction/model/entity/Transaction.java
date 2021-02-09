package io.assessment.banking.transaction.model.entity;

import java.time.ZonedDateTime;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import io.assessment.banking.transaction.constant.TransactionType;
import lombok.Data;

/**
 * Models the Transaction entity
 *
 * @author Nikhil Vibhav
 */
@Data
@Entity(name = "transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Double amount;

  @Enumerated
  @Column(nullable = false)
  private TransactionType type;

  @Column(nullable = false)
  private Long accountId;

  @Column(nullable = false)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private final ZonedDateTime dateTransacted = ZonedDateTime.now();
}
