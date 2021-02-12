package io.assessment.banking.facade.account;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.exception.account.AccountCreationFailedException;
import io.assessment.banking.exception.account.AccountNotFoundException;
import io.assessment.banking.exception.account.LowBalanceException;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.assessment.banking.service.account.AccountService;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

/**
 * Unit tests for {@link AccountFacade}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
public class AccountFacadeTest extends AbstractTest {

  @Mock private AccountService accountService;
  @Mock private TransactionService transactionService;
  @Mock private CustomerService customerService;

  @InjectMocks private AccountFacade facade;

  @Test
  public void givenValidParams_WhenSaveCurrentAccount_ThenSucceed() throws Exception {
    // Given
    final Customer customer = getCustomer();
    final Account updatedAccount = getAccount(customer);
    updatedAccount.setBalance(10.0);
    updatedAccount.setDateUpdated(ZonedDateTime.now());

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, 10.0, TransactionType.CREDIT))
        .willReturn(updatedAccount);
    given(transactionService.saveTransaction(any(TransactionVO.class)))
        .willReturn(getTransactionVO());

    // When
    final AccountVO savedAccount = assertDoesNotThrow(() -> facade.saveCurrentAccount(1L, 10.0));

    // Then
    assertNotNull(savedAccount);
    assertEquals(updatedAccount.getId(), savedAccount.getId());
    assertEquals(updatedAccount.getType(), savedAccount.getType());
    assertEquals(updatedAccount.getBalance(), savedAccount.getBalance());
  }

  @Test
  public void givenValidParams_WhenSaveCurrentAccount_ThenThrowCustomerNotFoundException()
      throws Exception {
    // Given
    given(customerService.getCustomerById(1L)).willThrow(CustomerNotFoundException.class);

    // When - Then
    assertThrows(CustomerNotFoundException.class, () -> facade.saveCurrentAccount(1L, 10.0));
  }

  @Test
  public void givenValidParams_WhenSaveCurrentAccount_ThenThrowLowBalanceException()
      throws Exception {
    // Given
    final Customer customer = getCustomer();
    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, 10.0, TransactionType.CREDIT))
        .willThrow(LowBalanceException.class);

    // When - Then
    assertThrows(LowBalanceException.class, () -> facade.saveCurrentAccount(1L, 10.0));
  }

  @Test
  public void givenValidParams_WhenSaveCurrentAccount_ThenThrowAccountNotFoundException()
      throws Exception {
    // Given
    final Customer customer = getCustomer();
    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, 10.0, TransactionType.CREDIT))
        .willThrow(AccountNotFoundException.class);

    // When - Then
    assertThrows(AccountNotFoundException.class, () -> facade.saveCurrentAccount(1L, 10.0));
  }

  @Test
  public void givenValidParams_WhenSaveCurrentAccount_ThenThrowAccountCreationFailedException()
      throws Exception {
    // Given
    final Customer customer = getCustomer();
    final Account updatedAccount = getAccount(customer);
    updatedAccount.setBalance(10.0);
    updatedAccount.setDateUpdated(ZonedDateTime.now());

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, 10.0, TransactionType.CREDIT))
        .willReturn(updatedAccount);
    given(transactionService.saveTransaction(any(TransactionVO.class)))
        .willThrow(TransactionServiceRestException.class);

    // When - Then
    assertThrows(AccountCreationFailedException.class, () -> facade.saveCurrentAccount(1L, 10.0));
  }

  @Test
  public void
      givenValidParams_WhenSaveCurrentAccountFailsAndRollback_ThenThrowAccountNotFoundException()
          throws Exception {
    // Given
    final Customer customer = getCustomer();
    final Account updatedAccount = getAccount(customer);
    updatedAccount.setBalance(10.0);
    updatedAccount.setDateUpdated(ZonedDateTime.now());

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, 10.0, TransactionType.CREDIT))
        .willReturn(updatedAccount);
    given(transactionService.saveTransaction(any(TransactionVO.class)))
        .willThrow(TransactionServiceRestException.class);
    willThrow(AccountNotFoundException.class).given(accountService).deleteAccount(1L);

    // When - Then
    assertThrows(AccountNotFoundException.class, () -> facade.saveCurrentAccount(1L, 10.0));
  }
}
