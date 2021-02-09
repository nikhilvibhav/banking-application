package io.assessment.banking.transaction.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.assessment.banking.transaction.exception.TransactionNotFoundException;
import io.assessment.banking.transaction.repository.TransactionRepository;
import io.assessment.banking.transaction.service.TransactionService;
import io.assessment.banking.transaction.model.entity.Transaction;
import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the {@link TransactionService} interface
 *
 * @author Nikhil Vibhav
 */
@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository repository;

  @Autowired
  public TransactionServiceImpl(final TransactionRepository repository) {
    this.repository = repository;
  }

  /**
   * Saves a transaction to the database
   *
   * @param transactionToSave - transaction received in the request
   * @return the saved {@link Transaction} entity
   */
  @Override
  public Transaction saveTransaction(final Transaction transactionToSave) {

    final Transaction savedTransaction = repository.save(transactionToSave);

    log.debug("Saved to database: {}", savedTransaction);
    return savedTransaction;
  }

  /**
   * Gets all the transactions filtered by the given account id
   *
   * @param accountId - the account id in the request
   * @return List of all the transactions that meet the given criteria
   */
  @Override
  public List<Transaction> findAllTransactionsByAccountId(final Long accountId) {

    final List<Transaction> transactions = repository.findAllByAccountId(accountId);

    log.debug("Transactions by accountId {}: {}", accountId, transactions);
    return transactions;
  }

  /**
   * Deletes a transaction by the given id
   *
   * @param id - the given id
   * @throws TransactionNotFoundException - when the application cannot find the transaction in the
   *     database
   */
  @Override
  @Transactional
  public void deleteTransaction(final Long id) throws TransactionNotFoundException {
    final Transaction transactionToDelete =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new TransactionNotFoundException(
                        "Unable to find any transactions with id: " + id));

    log.debug("Deleting the transaction {}", transactionToDelete);
    repository.delete(transactionToDelete);
  }
}
