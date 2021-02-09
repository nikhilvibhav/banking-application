package io.assessment.banking.transaction.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.assessment.banking.transaction.controller.mapper.TransactionMapper;
import io.assessment.banking.transaction.exception.TransactionNotFoundException;
import io.assessment.banking.transaction.model.entity.Transaction;
import io.assessment.banking.transaction.model.vo.TransactionVO;
import io.assessment.banking.transaction.service.TransactionService;
import lombok.extern.log4j.Log4j2;

/**
 * REST controller to handle the CRUD operations on transactions made in a bank account
 *
 * @author Nikhil Vibhav
 */
@RestController
@RequestMapping(path = "/api/bank/v1/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Log4j2
public class TransactionController {

  private final TransactionService transactionService;

  @Autowired
  public TransactionController(final TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  /**
   * Saves a transaction to the database
   *
   * @param request - the transaction request
   * @return the saved {@link TransactionVO} object
   */
  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TransactionVO> saveTransaction(
      @RequestBody @Valid final TransactionVO request) {

    log.info("Received request to save transaction: {}", request);

    final Transaction savedTransaction =
        transactionService.saveTransaction(TransactionMapper.toTransaction(request));

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(TransactionMapper.toTransactionVO(savedTransaction));
  }

  /**
   * Gets the list of all transactions by the given account id
   *
   * @param accountId - the account id for which to fetch the list of all transactions
   * @return a {@link List} of the transactions
   */
  @GetMapping
  public ResponseEntity<List<TransactionVO>> getByAccountId(
      @RequestParam @Min(1) final Long accountId) {

    log.info("Received request to get transactions by accountId - {}", accountId);
    final List<Transaction> transactions =
        transactionService.findAllTransactionsByAccountId(accountId);

    return ResponseEntity.ok(TransactionMapper.transactionVOs(transactions));
  }

  /**
   * Deletes a transaction by the given id
   *
   * @param id - the given transaction id
   * @throws TransactionNotFoundException - thrown when the service layer cannot find any
   *     transactions in the database
   */
  @DeleteMapping(path = "/{id}")
  public void deleteById(@PathVariable @Min(1) final Long id) throws TransactionNotFoundException {

    log.info("Deleting a transaction with the id: {}", id);
    transactionService.deleteTransaction(id);
  }
}
