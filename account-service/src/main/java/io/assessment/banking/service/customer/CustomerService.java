package io.assessment.banking.service.customer;

import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.model.customer.entity.Customer;

/**
 * Abstracts the functionalities to be performed on the {@link Customer} entity
 *
 * @author Nikhil Vibhav
 */
public interface CustomerService {
  Customer getCustomerById(final Long id) throws CustomerNotFoundException;
}
