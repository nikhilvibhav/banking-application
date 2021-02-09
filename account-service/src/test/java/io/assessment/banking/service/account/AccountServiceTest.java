package io.assessment.banking.service.account;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.repository.account.AccountRepository;
import io.assessment.banking.service.account.impl.AccountServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

/**
 * Junit tests for {@link AccountService}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends AbstractTest {

  @Mock private AccountRepository repository;
  @InjectMocks private AccountServiceImpl accountService;

  @Test
  public void givenValidAccount_WhenCreateAccount_ThenSucceed() {
    final Customer customer = getCustomer();
    final Account account = getAccount(customer);

    given(repository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

    final Account savedAccount = accountService.createAccount(account);

    assertEquals(1L, savedAccount.getId());
  }

  @Test
  public void givenValidAccount_WhenUpdateBalance_ThenSucceed() {

    given(repository.findById(1L)).willReturn(Optional.of(getAccount(getCustomer())));
    given(repository.save(any(Account.class)))
        .willAnswer(
            invocation -> {
              Account account = invocation.getArgument(0);
              account.setBalance(10.0);
              return account;
            });

    final Account updatedAccount =
        assertDoesNotThrow(() -> accountService.updateBalance(1L, 10.0, TransactionType.CREDIT));

    assertEquals(10.0, updatedAccount.getBalance());
  }

  @Test
  public void givenValidAccount_WhenUpdateBalance_ThenFail() {
    given(repository.findById(1L)).willReturn(Optional.of(getAccount(getCustomer())));

    assertThrows(
        LowBalanceException.class,
        () -> accountService.updateBalance(1L, 10.0, TransactionType.DEBIT),
        "Expected updateBalance() to throw LowBalanceException, but it didn't");
  }

  @Test
  public void givenValidAccountId_WhenDeleteAccount_ThenSucceed() {
    given(repository.findById(1L)).willReturn(Optional.of(getAccount(getCustomer())));
    willDoNothing().given(repository).delete(any(Account.class));

    assertDoesNotThrow(() -> accountService.deleteAccount(1L));
  }
}
