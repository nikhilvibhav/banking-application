package io.assessment.banking.transaction.service;

import java.util.List;

import io.assessment.banking.transaction.exception.TransactionNotFoundException;
import io.assessment.banking.transaction.model.entity.Transaction;

/**
 * Interface to define abstract functionalities on the Transaction entity
 *
 * @author Nikhil Vibhav
 */
public interface TransactionService {

  Transaction saveTransaction(final Transaction transactionToSave);

  List<Transaction> findAllTransactionsByAccountId(final Long accountId);

  void deleteTransaction(final Long id) throws TransactionNotFoundException;
}
