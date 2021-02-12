package io.assessment.banking.controller.customer;

import java.time.ZonedDateTime;
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
import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.facade.customer.CustomerFacade;
import io.assessment.banking.facade.customer.mapper.CustomerMapper;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.vo.CustomerVO;

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

  @MockBean private CustomerFacade facade;

  @Test
  public void givenCustomerId_WhenGetCustomer_ThenReturn200_Success() throws Exception {
    // Given
    final CustomerVO customerWithAccount = CustomerMapper.toCustomerVO(getCustomer());
    final AccountVO savedAccount = new AccountVO();
    savedAccount.setCustomerId(customerWithAccount.getId());
    savedAccount.setType(AccountType.CURRENT);
    savedAccount.setBalance(20.0);
    savedAccount.setDateUpdated(ZonedDateTime.now());
    customerWithAccount.setAccounts(Collections.singletonList(savedAccount));

    given(facade.getCustomerById(1L)).willReturn(customerWithAccount);

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.accounts[0].customerId").value("1"));

    verify(facade, times(1)).getCustomerById(1L);
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
  public void givenIncorrectCustomerId_WhenGetCustomer_ThenReturn404_CustomerNotFound()
      throws Exception {
    // Given
    given(facade.getCustomerById(1L)).willThrow(CustomerNotFoundException.class);

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(CUSTOMER_URI + "/1"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(status().reason("Could not find customer with the given id"));

    verify(facade, times(1)).getCustomerById(1L);
  }
}
