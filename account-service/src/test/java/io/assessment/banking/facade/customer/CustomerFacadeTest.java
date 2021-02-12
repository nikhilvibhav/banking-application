package io.assessment.banking.facade.customer;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.customer.vo.CustomerVO;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link CustomerFacade}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
public class CustomerFacadeTest extends AbstractTest {

  @Mock private CustomerService customerService;
  @Mock private TransactionService transactionService;

  @InjectMocks private CustomerFacade facade;

  @Test
  public void givenValidCustomerId_WhenGetCustomerById_ThenSucceed() throws Exception {
    // Given
    final Customer customer = getCustomer();
    customer.setAccounts(Collections.singletonList(getAccount(customer)));
    final long customerId = 1L;

    given(customerService.getCustomerById(1L)).willReturn(customer);
    given(transactionService.getAllTransactions(anyLong()))
        .willReturn(Collections.singletonList(getTransactionVO()));

    // When
    final CustomerVO customerFromDB = assertDoesNotThrow(() -> facade.getCustomerById(customerId));

    // Then
    assertNotNull(customerFromDB);
    assertEquals(customerId, customerFromDB.getId());
    assertEquals("John Doe", customerFromDB.getFirstName() + " " + customerFromDB.getSurname());
    assertTrue(customerFromDB.getAccounts().size() > 0);
  }

  @Test
  public void givenValidCustomerId_WhenGetCustomerById_ThenThrowCustomerNotFoundException()
      throws Exception {
    // Given
    given(customerService.getCustomerById(1L)).willThrow(CustomerNotFoundException.class);

    // When - Then
    assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1L));
  }
}
