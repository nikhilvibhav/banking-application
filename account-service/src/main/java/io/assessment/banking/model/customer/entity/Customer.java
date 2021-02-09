package io.assessment.banking.model.customer.entity;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import io.assessment.banking.model.account.entity.Account;
import lombok.Data;

/**
 * Models the Customer entity
 *
 * @author Nikhil Vibhav
 */
@Data
@Entity(name = "customer")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String surname;

  @Column(nullable = false)
  private String email;

  @OneToMany(mappedBy = "customer")
  @Fetch(FetchMode.SUBSELECT) // Helps with n+1 problem in ORMs
  private List<Account> accounts;

  @Column(nullable = false)
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private final ZonedDateTime dateCreated = ZonedDateTime.now();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private ZonedDateTime dateUpdated;
}
