package io.assessment.banking.controller.account;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.assessment.banking.exception.account.*;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.facade.account.AccountFacade;
import io.assessment.banking.model.account.vo.AccountVO;
import lombok.extern.log4j.Log4j2;

/**
 * REST Controller for performing RESTful operations on Bank Accounts
 *
 * @author Nikhil Vibhav
 */
@RestController
@RequestMapping(path = "/api/bank/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class AccountController {

  private final AccountFacade facade;

  @Autowired
  public AccountController(final AccountFacade facade) {
    this.facade = facade;
  }

  /**
   * This creates a CURRENT account
   *
   * @param request - The Account creation request
   * @return the {@link AccountVO} value object
   * @throws AccountNotFoundException - when the application cannot find the account by the given id
   * @throws LowBalanceException - when the account has insufficient balance to perform the
   *     operation
   * @throws CustomerNotFoundException - when the application cannot find the customer by the given
   *     id
   * @throws CreditTooLowException - when the initial credit in the request is 0
   * @throws AccountCreationFailedException - thrown when the saveTransaction failed and returns an
   *     error response to the user
   */
  @CrossOrigin("http://localhost:3000")
  @PostMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountVO> openCurrentAccount(@Valid @RequestBody final AccountVO request)
      throws AccountNotFoundException, LowBalanceException, CustomerNotFoundException,
          CreditTooLowException, AccountCreationFailedException {

    log.info("Received request for opening a current bank account: {}", request);

    if (request.getInitialCredit() == 0) {
      throw new CreditTooLowException("Initial Credit is too low to open the account");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(facade.saveCurrentAccount(request.getCustomerId(), request.getInitialCredit()));
  }
}
