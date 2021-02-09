package io.assessment.banking;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.transaction.vo.TransactionVO;

/**
 * Class that contains some of the common methods used in the unit tests
 *
 * @author Nikhil Vibhav
 */
public abstract class AbstractTest {

  public Customer getCustomer() {
    final Customer customer = new Customer();
    customer.setFirstName("John");
    customer.setSurname("Doe");
    customer.setId(1L);
    customer.setEmail("johndoe@example.com");
    customer.setAccounts(new ArrayList<>());
    return customer;
  }

  public TransactionVO getTransactionVO() {
    final TransactionVO transactionVO = new TransactionVO();
    transactionVO.setAccountId(1L);
    transactionVO.setAmount(10.0);
    transactionVO.setId(12L);
    transactionVO.setType(TransactionType.CREDIT);
    transactionVO.setDateTransacted(ZonedDateTime.now());
    return transactionVO;
  }

  public Account getAccount(final Customer customer) {
    final Account account = new Account();
    account.setId(1L);
    account.setBalance(0.0);
    account.setCustomer(customer);
    account.setType(AccountType.CURRENT);
    return account;
  }
}
