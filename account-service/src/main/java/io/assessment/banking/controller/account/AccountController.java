package io.assessment.banking.controller.account;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.controller.account.mapper.AccountMapper;
import io.assessment.banking.exception.account.*;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.assessment.banking.service.account.AccountService;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;
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

  private final AccountService accountService;
  private final TransactionService transactionService;
  private final CustomerService customerService;

  @Autowired
  public AccountController(
      final AccountService accountService,
      final TransactionService transactionService,
      final CustomerService customerService) {
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.customerService = customerService;
  }

  /**
   * This creates a CURRENT account
   *
   * @param request - The Account creation request
   * @return the {@link AccountVO} object
   * @throws AccountNotFoundException - when the application cannot find the account by the given id
   * @throws LowBalanceException - when the account has insufficient balance to perform the
   *     operation
   * @throws CustomerNotFoundException - when the application cannot find the customer by the given
   *     id
   * @throws CreditTooLowException - when the initial credit in the request is 0
   */
  @PostMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountVO> openCurrentAccount(@Valid @RequestBody final AccountVO request)
      throws AccountNotFoundException, LowBalanceException, CustomerNotFoundException,
          CreditTooLowException, AccountCreationFailedException {

    log.info("Received request for opening a current bank account: {}", request);

    if (request.getInitialCredit() == 0) {
      throw new CreditTooLowException("Initial Credit is too low to open the account");
    }

    final Customer customer = customerService.getCustomerById(request.getCustomerId());
    final Account savedAccount =
        accountService.createAccount(AccountMapper.toAccount(customer, AccountType.CURRENT));
    final Account updatedAccount =
        accountService.updateBalance(
            savedAccount.getId(), request.getInitialCredit(), TransactionType.CREDIT);

    saveTransaction(request.getInitialCredit(), savedAccount.getId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AccountMapper.toAccountVO(updatedAccount));
  }

  /**
   * Calls transaction service to save the transaction
   *
   * @param amount - the amount to save
   * @param accountId - the account id against which to save the transaction
   * @throws AccountNotFoundException - when the application cannot find the account by the given id
   * @throws AccountCreationFailedException - thrown when the saveTransaction failed and returns an
   *     error response to the user
   */
  private void saveTransaction(final double amount, final long accountId)
      throws AccountNotFoundException, AccountCreationFailedException {
    try {
      final TransactionVO transactionToSave = new TransactionVO();
      transactionToSave.setAccountId(accountId);
      transactionToSave.setType(TransactionType.CREDIT);
      transactionToSave.setAmount(amount);

      final TransactionVO savedTransaction = transactionService.saveTransaction(transactionToSave);
      log.debug("Save transaction response {}", savedTransaction);
    } catch (TransactionServiceRestException | TransactionServiceInvalidResponseException e) {
      // Rollback account creation
      rollbackAccountCreation(accountId);
    }
  }

  /**
   * Rollback the account created
   *
   * @param accountId - the account id to rollback
   * @throws AccountNotFoundException - thrown when the service layer could not find the account
   *     based on the id
   * @throws AccountCreationFailedException - thrown when the saveTransaction failed and returns an
   *     error response to the user
   */
  private void rollbackAccountCreation(final long accountId)
      throws AccountNotFoundException, AccountCreationFailedException {

    log.error(
        "Error occurred while trying to save the transaction entity for accountId: {}, rolling back account creation...",
        accountId);

    accountService.deleteAccount(accountId);
    log.info("Deleted account for accountId: {}", accountId);

    throw new AccountCreationFailedException(
        "Error occurred while trying to save the transaction entity for the given accountId");
  }
}
