package io.assessment.banking.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the application cannot find the required transaction in the database
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    code = HttpStatus.NOT_FOUND,
    reason = "Unable to find any transactions with the given id")
public class TransactionNotFoundException extends Exception {
  public TransactionNotFoundException(final String message) {
    super(message);
  }
}
