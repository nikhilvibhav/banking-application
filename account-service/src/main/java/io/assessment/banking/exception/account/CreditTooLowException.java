package io.assessment.banking.exception.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.assessment.banking.model.account.vo.AccountVO;

/**
 * Exception thrown when {@link AccountVO#getInitialCredit()} is too low to open an account
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    value = HttpStatus.BAD_REQUEST,
    reason = "Initial Credit is too low to open the account")
public class CreditTooLowException extends Exception {
  public CreditTooLowException(final String message) {
    super(message);
  }
}
