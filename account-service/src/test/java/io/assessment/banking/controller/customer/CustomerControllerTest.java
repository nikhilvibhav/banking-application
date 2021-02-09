package io.assessment.banking.controller.customer;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link CustomerController}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest extends AbstractTest {

  private static final String CUSTOMER_URI = "/api/bank/v1/customer";

  @Autowired private MockMvc mockMvc;

  @MockBean private CustomerService customerService;
  @MockBean private TransactionService transactionService;

  @Test
  public void givenCustomerId_WhenGetCustomer_ThenReturn200_Success() throws Exception {
    // Given
    final Customer customerWithAccount = getCustomer();
    customerWithAccount.setAccounts(Collections.singletonList(getAccount(customerWithAccount)));

    given(customerService.getCustomerById(1L)).willReturn(customerWithAccount);
    given(transactionService.getAllTransactions(1L))
        .willReturn(Collections.singletonList(getTransactionVO()));

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.accounts[0].customerId").value("1"));

    verify(customerService, times(1)).getCustomerById(1L);
    verify(transactionService, times(1)).getAllTransactions(1L);
  }

  @Test
  public void givenNoCustomerId_WhenGetCustomer_ThenReturn404_NotFound() throws Exception {
    // Given

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void givenInvalidCustomerId_WhenGetCustomer_ThenReturn400_BadRequest() throws Exception {
    // Given

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/0"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errors[0]")
                .value(
                    "CustomerController#getCustomerById.id: must be greater than or equal to 1"));
  }

  @Test
  public void givenIncorrectCustomerId_WhenGetCustomer_ThenReturn500_CustomerNotFound()
      throws Exception {
    // Given
    given(customerService.getCustomerById(1L)).willThrow(CustomerNotFoundException.class);

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/1"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(status().reason("Could not find customer with the given id"));

    verify(customerService, times(1)).getCustomerById(1L);
  }
}
