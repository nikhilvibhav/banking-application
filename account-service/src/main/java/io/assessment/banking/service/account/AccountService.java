package io.assessment.banking.service.account;

import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.AccountNotFoundException;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.model.account.entity.Account;

/**
 * Interface to house the abstract functionalities on the {@link Account} entity
 *
 * @author Nikhil Vibhav
 */
public interface AccountService {

  Account createAccount(final Account accountToSave);

  Account updateBalance(final Long accountId, final Double amount, final TransactionType type)
      throws AccountNotFoundException, LowBalanceException;

  void deleteAccount(final Long accountId) throws AccountNotFoundException;
}
