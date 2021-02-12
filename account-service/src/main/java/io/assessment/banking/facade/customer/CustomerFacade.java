package io.assessment.banking.facade.customer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.facade.account.mapper.AccountMapper;
import io.assessment.banking.facade.customer.mapper.CustomerMapper;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.customer.vo.CustomerVO;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;

/**
 * Facade class to hide the complexity of calling different service layers for getting a customer
 *
 * @author Nikhil Vibhav
 */
@Component
@Log4j2
public class CustomerFacade {

  private final CustomerService customerService;
  private final TransactionService transactionService;

  @Autowired
  public CustomerFacade(
      final CustomerService customerService, final TransactionService transactionService) {
    this.customerService = customerService;
    this.transactionService = transactionService;
  }

  /**
   * Gets the customer by the given id
   *
   * @param id - the given customer id
   * @return the {@link CustomerVO} value object containing the accounts and transactions, if any
   * @throws CustomerNotFoundException - thrown when the application cannot find the customer by the
   *     given id
   */
  public CustomerVO getCustomerById(final Long id) throws CustomerNotFoundException {
    final Customer customer = customerService.getCustomerById(id);

    final CustomerVO customerResponse = CustomerMapper.toCustomerVO(customer);
    customerResponse.setAccounts(getAccountsWithTransactions(customer));

    return customerResponse;
  }

  /**
   * Returns a list of {@link AccountVO} containing the transactions for the customer
   *
   * @param customer - the customer as retrieved from the DB
   * @return a list of {@link AccountVO} containing the transactions against that account
   */
  private List<AccountVO> getAccountsWithTransactions(final Customer customer) {
    return customer.getAccounts().stream()
        .map(mapTransactionsToAccount())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * {@link Function} to get the transactions for an account and map it to {@link AccountVO} value
   * object
   *
   * @return {@link AccountVO} value objects with the transactions, if an error occurs while calling
   *     transaction service, returns null
   */
  private Function<Account, AccountVO> mapTransactionsToAccount() {
    return account -> {
      try {
        final AccountVO accountVO = AccountMapper.toAccountVO(account);
        accountVO.setTransactions(transactionService.getAllTransactions(account.getId()));
        return accountVO;
      } catch (TransactionServiceRestException | TransactionServiceInvalidResponseException e) {
        log.error("Couldn't get transactions for accountId: {}, ignoring...", account.getId());
        return null;
      }
    };
  }
}
