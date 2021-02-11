package io.assessment.banking.controller.customer;

import java.util.List;
import java.util.Objects;
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
  @CrossOrigin("http://localhost:3000")
  @GetMapping(path = "/{id}")
  public ResponseEntity<CustomerVO> getCustomerById(@PathVariable @Min(1) final Long id)
      throws CustomerNotFoundException {

    log.info("Received request to get customer by id: {}", id);
    final Customer customer = customerService.getCustomerById(id);

    final CustomerVO customerResponse = CustomerMapper.toCustomerVO(customer);
    customerResponse.setAccounts(getAccountsWithTransactions(customer));

    return ResponseEntity.ok(customerResponse);
  }

  /**
   * Returns a list of {@link AccountVO} containing the transactions for the customer
   *
   * @param customer - the customer as retrieved from the DB
   * @return a list of {@link AccountVO} containing the transactions against that account
   */
  private List<AccountVO> getAccountsWithTransactions(final Customer customer) {
    return customer.getAccounts().stream()
        .map(mapTransactionsToAccount())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * {@link Function} to get the transactions for an account and map it to {@link AccountVO} value
   * object
   *
   * @return {@link AccountVO} value objects with the transactions, if an error occurs while calling
   *     transaction service, returns null
   */
  private Function<Account, AccountVO> mapTransactionsToAccount() {
    return account -> {
      try {
        final AccountVO accountVO = AccountMapper.toAccountVO(account);
        accountVO.setTransactions(transactionService.getAllTransactions(account.getId()));
        return accountVO;
      } catch (TransactionServiceRestException | TransactionServiceInvalidResponseException e) {
        log.error("Couldn't get transactions for accountId: {}, ignoring...", account.getId());
        return null;
      }
    };
  }
}
