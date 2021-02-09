package io.assessment.banking.service.transaction;

import java.util.List;

import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.model.transaction.vo.TransactionVO;

/**
 * Interface to house the abstract functionalities made over the network to the
 * transaction-service's APIs
 *
 * @author Nikhil Vibhav
 */
public interface TransactionService {
  TransactionVO saveTransaction(final TransactionVO transaction)
      throws TransactionServiceRestException, TransactionServiceInvalidResponseException;

  List<TransactionVO> getAllTransactions(final Long accountId)
      throws TransactionServiceRestException, TransactionServiceInvalidResponseException;

  void deleteTransaction(final Long id) throws TransactionServiceRestException;
}
