package io.assessment.banking.transaction;

import io.assessment.banking.transaction.constant.TransactionType;
import io.assessment.banking.transaction.model.entity.Transaction;

/**
 * Abstract class that contains some of the common methods used in the unit tests
 *
 * @author Nikhil Vibhav
 */
public abstract class AbstractTest {

  public Transaction getTransaction() {
    final Transaction transaction = new Transaction();
    transaction.setAmount(10.0);
    transaction.setAccountId(1L);
    transaction.setType(TransactionType.CREDIT);
    return transaction;
  }
}
