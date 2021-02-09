package io.assessment.banking.controller.customer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.assessment.banking.controller.account.mapper.AccountMapper;
import io.assessment.banking.controller.customer.mapper.CustomerMapper;
import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.account.vo.AccountVO;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.customer.vo.CustomerVO;
import io.assessment.banking.service.customer.CustomerService;
import io.assessment.banking.service.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;

/**
 * REST controller to perform RESTful operations on the Customer resource
 *
 * @author Nikhil Vibhav
 */
@RestController
@RequestMapping(path = "/api/bank/v1/customer", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Log4j2
public class CustomerController {

  private final CustomerService customerService;
  private final TransactionService transactionService;

  @Autowired
  public CustomerController(
      final CustomerService customerService, final TransactionService transactionService) {
    this.customerService = customerService;
    this.transactionService = transactionService;
  }

  /**
   * Gets a {@link Customer} with the linked {@link Account} and Transactions
   *
   * @param id - The customer id
   * @return the customer object
   * @throws CustomerNotFoundException when the application cannot find the customer by the given id
   */
  @GetMapping(path = "/{id}")
  public ResponseEntity<CustomerVO> getCustomerById(@PathVariable @Min(1) final Long id)
      throws CustomerNotFoundException {

    log.info("Received request to get customer by id: {}", id);
    final Customer customer = customerService.getCustomerById(id);

    final List<AccountVO> accounts =
        customer.getAccounts().stream().map(accountFunction()).collect(Collectors.toList());

    final CustomerVO customerResponse = CustomerMapper.toCustomerVO(customer);
    customerResponse.setAccounts(accounts);

    return ResponseEntity.ok(customerResponse);
  }

  // TODO: Fix error handling refer - https://www.baeldung.com/java-lambda-exceptions
  private Function<Account, AccountVO> accountFunction() {
    return account -> {
      try {
        final AccountVO accountVO = AccountMapper.toAccountVO(account);
        accountVO.setTransactions(transactionService.getAllTransactions(account.getId()));
        return accountVO;
      } catch (TransactionServiceRestException | TransactionServiceInvalidResponseException e) {
        throw new RuntimeException("An exception occurred while calling transaction-service");
      }
    };
  }
}
