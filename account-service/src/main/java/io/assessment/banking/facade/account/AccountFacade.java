package io.assessment.banking.facade.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.AccountCreationFailedException;
import io.assessment.banking.exception.account.AccountNotFoundException;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.facade.account.mapper.AccountMapper;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.assessment.banking.service.account.AccountService;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;

/**
 * Facade class to hide the complexity of calling different service layers for creating an account
 *
 * @author Nikhil Vibhav
 */
@Component
@Log4j2
public class AccountFacade {

  private final AccountService accountService;
  private final TransactionService transactionService;
  private final CustomerService customerService;

  @Autowired
  public AccountFacade(
      final AccountService accountService,
      final TransactionService transactionService,
      final CustomerService customerService) {
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.customerService = customerService;
  }

  /**
   * Saves a current account
   *
   * @param customerId - the customerId in the request
   * @param initialCredit - the initialCredit in the request
   * @return the updated {@link AccountVO} value object with the transaction and balance
   * @throws AccountNotFoundException - when the application cannot find the account by the given id
   * @throws LowBalanceException - when the account has insufficient balance to perform the
   *     operation
   * @throws CustomerNotFoundException - when the application cannot find the customer by the given
   *     id
   * @throws AccountCreationFailedException - thrown when the saveTransaction failed and returns an
   *     error response to the user
   */
  public AccountVO saveCurrentAccount(final Long customerId, final Double initialCredit)
      throws CustomerNotFoundException, AccountNotFoundException, LowBalanceException,
          AccountCreationFailedException {

    final Customer customer = customerService.getCustomerById(customerId);
    final Account savedAccount =
        accountService.createAccount(AccountMapper.toAccount(customer, AccountType.CURRENT));
    final Account updatedAccount =
        accountService.updateBalance(savedAccount.getId(), initialCredit, TransactionType.CREDIT);

    saveTransaction(initialCredit, savedAccount.getId());

    return AccountMapper.toAccountVO(updatedAccount);
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
