package io.assessment.banking.transaction.controller.mapper;

import java.util.List;
import java.util.stream.Collectors;

import io.assessment.banking.transaction.model.vo.TransactionVO;
import io.assessment.banking.transaction.model.entity.Transaction;

/**
 * Maps object of {@link TransactionVO} to {@link Transaction} and vice-versa
 *
 * @author Nikhil Vibhav
 */
public class TransactionMapper {

  public static Transaction toTransaction(final TransactionVO transactionVO) {
    final Transaction transaction = new Transaction();
    transaction.setAccountId(transactionVO.getAccountId());
    transaction.setAmount(transactionVO.getAmount());
    transaction.setType(transactionVO.getType());

    return transaction;
  }

  public static TransactionVO toTransactionVO(final Transaction transaction) {
    final TransactionVO transactionVO = new TransactionVO();
    transactionVO.setId(transaction.getId());
    transactionVO.setAccountId(transaction.getAccountId());
    transactionVO.setAmount(transaction.getAmount());
    transactionVO.setType(transaction.getType());
    transactionVO.setDateTransacted(transaction.getDateTransacted());

    return transactionVO;
  }

  public static List<TransactionVO> transactionVOs(final List<Transaction> transactions) {
    return transactions.stream()
        .map(TransactionMapper::toTransactionVO)
        .collect(Collectors.toList());
  }
}
