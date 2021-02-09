package io.assessment.banking.util.transaction;

import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.service.transaction.TransactionService;

/**
 * Util methods for {@link TransactionService} class
 *
 * @author Nikhil Vibhav
 */
public final class TransactionUtil {

  /**
   * Performs the DEBIT/CREDIT transaction
   *
   * @param amount - the amount to credit/debit
   * @param balance - the balance in the account
   * @param type - the transaction type - CREDIT/DEBIT
   * @throws LowBalanceException thrown when the account doesn't have sufficient balance to perform
   *     the operation
   */
  public static Double performTransaction(
      final Double amount, final Double balance, final TransactionType type)
      throws LowBalanceException {

    switch (type) {
      case CREDIT:
        return balance + amount;
      case DEBIT:
        if (balance < amount) {
          throw new LowBalanceException(
              "The account has low balance to perform the DEBIT operation");
        }
        return balance - amount;
      default:
        throw new IllegalStateException("Unexpected value: " + type);
    }
  }
}
