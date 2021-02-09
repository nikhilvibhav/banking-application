package io.assessment.banking.controller.account.mapper;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;

/**
 * Maps the object of {@link io.assessment.banking.model.account.entity.Account} to {@link
 * AccountVO} and vice-versa
 *
 * @author Nikhil Vibhav
 */
public class AccountMapper {

  /**
   * Maps the {@link AccountVO} object to {@link Account}
   *
   * @param customer - the customer object linked to the account
   * @param type - the account type - savings/credit
   * @return an object of {@link Account}
   */
  public static Account toAccount(final Customer customer, final AccountType type) {
    final Account account = new Account();
    account.setCustomer(customer);
    account.setType(type);
    return account;
  }

  /**
   * Maps {@link Account} object to {@link AccountVO} object
   *
   * @param account - the object to map of {@link Account}
   * @return mapped object of {@link AccountVO}
   */
  public static AccountVO toAccountVO(final Account account) {
    final AccountVO accountVO = new AccountVO();
    accountVO.setId(account.getId());
    accountVO.setCustomerId(account.getCustomer().getId());
    accountVO.setBalance(account.getBalance());
    accountVO.setType(account.getType());
    accountVO.setDateCreated(account.getDateCreated());
    accountVO.setDateUpdated(account.getDateUpdated());
    return accountVO;
  }
}
