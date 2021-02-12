package io.assessment.banking.controller.customer;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.assessment.banking.exception.customer.CustomerNotFoundException;
import io.assessment.banking.facade.customer.CustomerFacade;
import io.assessment.banking.model.account.entity.Account;
import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.customer.vo.CustomerVO;
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

  private final CustomerFacade facade;

  @Autowired
  public CustomerController(final CustomerFacade facade) {
    this.facade = facade;
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

    return ResponseEntity.ok(facade.getCustomerById(id));
  }
}
