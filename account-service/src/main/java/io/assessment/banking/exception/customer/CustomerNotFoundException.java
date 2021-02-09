package io.assessment.banking.exception.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a customer is not found in the database
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find customer with the given id")
public class CustomerNotFoundException extends Exception {

  public CustomerNotFoundException(final String message) {
    super(message);
  }
}
