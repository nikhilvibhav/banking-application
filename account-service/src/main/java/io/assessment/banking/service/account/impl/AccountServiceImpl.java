package io.assessment.banking.service.account.impl;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.AccountNotFoundException;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.repository.account.AccountRepository;
import io.assessment.banking.service.account.AccountService;
import io.assessment.banking.util.transaction.TransactionUtil;
import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the functionalities described in {@link AccountService} interface
 *
 * @author Nikhil Vibhav
 */
@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

  private final AccountRepository repository;

  @Autowired
  public AccountServiceImpl(final AccountRepository repository) {
    this.repository = repository;
  }

  /**
   * Saves the account to database
   *
   * @param accountToSave - the account received in the request
   * @return the saved {@link Account} entity
   */
  @Override
  public Account createAccount(final Account accountToSave) {

    final Account savedAccount = repository.save(accountToSave);

    log.debug("Saving account: {}", savedAccount);
    return savedAccount;
  }

  /**
   * Updates the balance in the account based on the {@link TransactionType}
   *
   * @param id - the account id
   * @param amount - the amount to credit/debit
   * @param type - the transaction type - CREDIT/DEBIT
   * @return the {@link Account} entity with the updated balance
   * @throws AccountNotFoundException - thrown when the application can't find an account with the
   *     given id
   * @throws LowBalanceException - thrown when the account doesn't have sufficient balance to
   *     perform the operation
   */
  @Override
  @Transactional
  public Account updateBalance(final Long id, final Double amount, final TransactionType type)
      throws AccountNotFoundException, LowBalanceException {

    final Account accountToUpdate = findAccount(id);

    accountToUpdate.setBalance(
        TransactionUtil.performTransaction(amount, accountToUpdate.getBalance(), type));
    accountToUpdate.setDateUpdated(ZonedDateTime.now());

    log.debug("Updating balance for account: {}", accountToUpdate);
    return repository.save(accountToUpdate);
  }

  /**
   * Delete the account by its account id
   *
   * @param id - the account id
   * @throws AccountNotFoundException - thrown when the application cannot find the account with the
   *     given id
   */
  @Override
  @Transactional
  public void deleteAccount(final Long id) throws AccountNotFoundException {
    final Account accountToDelete = findAccount(id);

    log.debug("Deleting account: {}", accountToDelete);
    repository.delete(accountToDelete);
  }

  /**
   * Finds the account by the given account id
   *
   * @param id - the account id
   * @return the {@link Account} entity
   * @throws AccountNotFoundException thrown when the application cannot find the account with the
   *     given id
   */
  private Account findAccount(final Long id) throws AccountNotFoundException {

    log.debug("Finding account with id: {}", id);
    return repository
        .findById(id)
        .orElseThrow(
            () -> new AccountNotFoundException("Could not find account with the given id: " + id));
  }
}
