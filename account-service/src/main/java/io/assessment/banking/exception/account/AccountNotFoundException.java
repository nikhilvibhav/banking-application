package io.assessment.banking.exception.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the required account cannot be found in the given database
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Could not find account with the given id")
public class AccountNotFoundException extends Exception {
  public AccountNotFoundException(final String message) {
    super(message);
  }
}
