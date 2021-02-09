package io.assessment.banking.transaction.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.assessment.banking.transaction.AbstractTest;
import io.assessment.banking.transaction.model.entity.Transaction;
import io.assessment.banking.transaction.repository.TransactionRepository;
import io.assessment.banking.transaction.service.impl.TransactionServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

/**
 * JUnit tests for {@link TransactionService}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest extends AbstractTest {

  @Mock private TransactionRepository repository;
  @InjectMocks private TransactionServiceImpl transactionService;

  @Test
  public void givenTransaction_WhenSaveTransaction_ThenSucceed() {
    final Transaction transaction = getTransaction();

    given(repository.save(any(Transaction.class)))
        .willAnswer(
            invocation -> {
              Transaction transaction1 = invocation.getArgument(0);
              transaction1.setId(2L);
              return transaction1;
            });

    final Transaction savedTransaction = transactionService.saveTransaction(transaction);
    assertEquals(2L, savedTransaction.getId());
    assertEquals(transaction.getAccountId(), savedTransaction.getAccountId());
  }

  @Test
  public void givenAccountId_WhenGetAllTransactionsByAccountId_ThenSucceed() {
    given(repository.findAllByAccountId(1L))
        .willAnswer(
            invocation -> {
              final Transaction transaction = getTransaction();
              transaction.setId(1L);
              return Collections.singletonList(transaction);
            });

    final List<Transaction> transactions = transactionService.findAllTransactionsByAccountId(1L);
    assertTrue(transactions.size() > 0);
  }

  @Test
  public void givenTransactionId_WhenDeleteTransaction_ThenSucceed() {
    willDoNothing().given(repository).delete(any(Transaction.class));
    given(repository.findById(1L)).willReturn(Optional.of(getTransaction()));

    assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));
  }
}
