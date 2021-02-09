package io.assessment.banking.controller.account;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.constant.transaction.TransactionType;
import io.assessment.banking.AbstractTest;
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

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link AccountController}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest extends AbstractTest {
  private static final String ACCOUNT_URI = "/api/bank/v1/account";

  @Autowired private MockMvc mockMvc;
  @Autowired private Gson gson;

  @MockBean private AccountService accountService;
  @MockBean private TransactionService transactionService;
  @MockBean private CustomerService customerService;

  @Test
  public void givenAccountRequest_WhenCreateCurrentAccount_ThenReturn201_Success()
      throws Exception {
    // Given
    // Mock objects
    final Customer customer = getCustomer();

    final Account updatedAccount = getAccount(customer);
    updatedAccount.setDateUpdated(ZonedDateTime.now());
    updatedAccount.setBalance(10.0);

    // Request
    final AccountVO request = new AccountVO();
    request.setCustomerId(1L);
    request.setInitialCredit(10.0);

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(transactionService.saveTransaction(any(TransactionVO.class)))
        .willReturn(getTransactionVO());
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT))
        .willReturn(updatedAccount);

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.balance").value("10.0"))
        .andExpect(jsonPath("$.type").value(AccountType.CURRENT.toString()));

    verify(customerService, times(1)).getCustomerById(1L);
    verify(transactionService, times(1)).saveTransaction(any(TransactionVO.class));
    verify(accountService, times(1)).createAccount(any(Account.class));
    verify(accountService, times(1))
        .updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT);
  }

  @Test
  public void givenInvalidAccountRequest_WhenCreateCurrentAccount_ThenReturn400_BadRequest()
      throws Exception {
    // Given
    // Request
    final AccountVO request = new AccountVO();
    request.setInitialCredit(-10.0);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("initialCredit cannot be negative")))
        .andExpect(content().string(containsString("customerId cannot be null")));
  }

  @Test
  public void
      givenAccountRequestWith0InitialCredit_WhenCreateCurrentAccount_ThenReturn400_CreditTooLowException()
          throws Exception {
    // Given
    final AccountVO request = new AccountVO();
    request.setCustomerId(1L);
    request.setInitialCredit(0.0);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0]").value("Initial Credit is too low to open the account"));
  }

  @Test
  public void givenAccountRequest_WhenCreateCurrentAccount_ThenReturn404_CustomerNotFoundException()
      throws Exception {
    // Given
    final AccountVO request = new AccountVO();
    request.setCustomerId(1L);
    request.setInitialCredit(10.0);

    given(customerService.getCustomerById(1L)).willThrow(CustomerNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());

    verify(customerService, times(1)).getCustomerById(1L);
  }

  @Test
  public void givenAccountRequest_WhenCreateCurrentAccount_ThenReturn500_LowBalanceException()
      throws Exception {

    // Given
    final Customer customer = getCustomer();

    final AccountVO request = new AccountVO();
    request.setCustomerId(1L);
    request.setInitialCredit(10.0);

    final Account updatedAccount = getAccount(customer);
    updatedAccount.setDateUpdated(ZonedDateTime.now());
    updatedAccount.setBalance(10.0);

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT))
        .willThrow(LowBalanceException.class);

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(status().reason("The account has low balance to perform the operation"));

    verify(customerService, times(1)).getCustomerById(1L);
    verify(accountService, times(1)).createAccount(any(Account.class));
    verify(accountService, times(1))
        .updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT);
  }

  @Test
  public void
      givenAccountRequest_WhenCreateCurrentAccount_ThenTransactionServiceReturns500_RollbackAndThrowAccountCreatingFailed()
          throws Exception {
    // Given
    // Mock objects
    final Customer customer = getCustomer();

    final Account updatedAccount = getAccount(customer);
    updatedAccount.setDateUpdated(ZonedDateTime.now());
    updatedAccount.setBalance(10.0);

    // Request
    final AccountVO request = new AccountVO();
    request.setCustomerId(1L);
    request.setInitialCredit(10.0);

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(transactionService.saveTransaction(any(TransactionVO.class)))
        .willThrow(TransactionServiceRestException.class);
    given(accountService.createAccount(any(Account.class))).willReturn(getAccount(customer));
    given(accountService.updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT))
        .willReturn(updatedAccount);
    willDoNothing().given(accountService).deleteAccount(1L);

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(ACCOUNT_URI + "/current")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(
            status()
                .reason(
                    "Error occurred while trying to save the transaction entity for given accountId"));

    verify(customerService, times(1)).getCustomerById(1L);
    verify(transactionService, times(1)).saveTransaction(any(TransactionVO.class));
    verify(accountService, times(1)).createAccount(any(Account.class));
    verify(accountService, times(1))
        .updateBalance(1L, request.getInitialCredit(), TransactionType.CREDIT);
    verify(accountService, times(1)).deleteAccount(1L);
  }
}
