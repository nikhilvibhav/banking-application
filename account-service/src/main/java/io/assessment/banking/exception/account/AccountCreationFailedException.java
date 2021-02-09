package io.assessment.banking.exception.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when REST call to Transaction Service fails
 *
 * @author Nikhil Vibhav
 */
@ResponseStatus(
    code = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "Error occurred while trying to save the transaction entity for given accountId")
public class AccountCreationFailedException extends Exception {
  public AccountCreationFailedException(final String message) {}
}
