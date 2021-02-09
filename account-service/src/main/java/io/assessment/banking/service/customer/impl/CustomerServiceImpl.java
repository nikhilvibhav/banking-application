package io.assessment.banking.service.customer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.repository.customer.CustomerRepository;
import io.assessment.banking.service.customer.CustomerService;
import lombok.extern.log4j.Log4j2;

/**
 * Implements the abstract functionalities defined in {@link CustomerService}
 *
 * @author Nikhil Vibhav
 */
@Service
@Log4j2
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  @Autowired
  public CustomerServiceImpl(final CustomerRepository repository) {
    this.repository = repository;
  }

  /**
   * Finds a customer by the given id
   *
   * @param id - the customer id
   * @return a {@link Customer}
   * @throws CustomerNotFoundException - when no customer with the given id exists in the database
   */
  @Override
  public Customer getCustomerById(final Long id) throws CustomerNotFoundException {

    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new CustomerNotFoundException("Could not find customer with the given id: " + id));
  }
}
