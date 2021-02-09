package io.assessment.banking.exception.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the REST call to Transaction Service fails
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "REST call to transaction-service failed")
public class TransactionServiceRestException extends Exception {

  public TransactionServiceRestException(final String message) {
    super(message);
  }
}
