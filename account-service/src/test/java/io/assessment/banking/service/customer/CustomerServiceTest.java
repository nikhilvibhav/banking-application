package io.assessment.banking.service.customer;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.repository.customer.CustomerRepository;
import io.assessment.banking.service.customer.impl.CustomerServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

/**
 * JUnit tests for {@link CustomerServiceTest}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest extends AbstractTest {

  @Mock private CustomerRepository repository;
  @InjectMocks private CustomerServiceImpl customerService;

  @Test
  public void givenCustomerId_WhenGetCustomerById_ThenSucceed() {
    given(repository.findById(1L)).willReturn(Optional.of(getCustomer()));

    final Customer customer = assertDoesNotThrow(() -> customerService.getCustomerById(1L));
    assertEquals(1L, customer.getId());
    assertEquals("johndoe@example.com", customer.getEmail());
  }
}
