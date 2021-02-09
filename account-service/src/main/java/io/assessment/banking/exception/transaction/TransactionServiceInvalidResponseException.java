package io.assessment.banking.exception.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the transaction service returns a response that is blank or doesn't have a
 * 2xx status code
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "The Transaction Service API returned an invalid response")
public class TransactionServiceInvalidResponseException extends Exception {
  public TransactionServiceInvalidResponseException(final String message) {
    super(message);
  }
}
