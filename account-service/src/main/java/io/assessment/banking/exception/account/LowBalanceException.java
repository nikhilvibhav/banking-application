package io.assessment.banking.exception.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the customer's account has too low balance to perform the DEBIT operation
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "The account has low balance to perform the operation")
public class LowBalanceException extends Exception {
  public LowBalanceException(final String message) {
    super(message);
  }
}
