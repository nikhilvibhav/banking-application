package io.assessment.banking.transaction.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.assessment.banking.transaction.AbstractTest;
import io.assessment.banking.transaction.constant.TransactionType;
import io.assessment.banking.transaction.exception.TransactionNotFoundException;
import io.assessment.banking.transaction.model.entity.Transaction;
import io.assessment.banking.transaction.model.vo.TransactionVO;
import io.assessment.banking.transaction.service.TransactionService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for {@link TransactionController}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest extends AbstractTest {

  private static final String TRANSACTION_URI = "/api/bank/v1/transaction";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private TransactionService transactionService;

  @Test
  public void givenValidTransaction_WhenSaveTransaction_ThenReturn201_Created() throws Exception {
    // Given
    final TransactionVO request = new TransactionVO();
    request.setAmount(10.0);
    request.setAccountId(1L);
    request.setType(TransactionType.CREDIT);

    given(transactionService.saveTransaction(any(Transaction.class)))
        .willAnswer(
            invocation -> {
              final Transaction response = getTransaction();
              response.setId(1L);
              return response;
            });

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(TRANSACTION_URI)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.accountId").value("1"));

    verify(transactionService, times(1)).saveTransaction(any(Transaction.class));
  }

  @Test
  public void givenInvalidTransaction_WhenSaveTransaction_ThenReturn400_BadRequest()
      throws Exception {
    // Given
    final TransactionVO request = new TransactionVO();

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(TRANSACTION_URI)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Error count - 3")))
        .andExpect(
            content()
                .string(
                    containsString("type cannot be null - it should either be CREDIT or DEBIT")))
        .andExpect(content().string(containsString("accountId cannot be null")))
        .andExpect(content().string(containsString("amount cannot be null")));
  }

  @Test
  public void givenTransactionWithNegativeAmount_WhenSaveTransaction_ThenReturn400_BadRequest()
      throws Exception {
    // Given
    final TransactionVO request = new TransactionVO();
    request.setAmount(-10.0);
    request.setAccountId(1L);
    request.setType(TransactionType.CREDIT);

    // Then
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(TRANSACTION_URI)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Error count - 1")))
        .andExpect(content().string(containsString("amount cannot be negative")));
  }

  @Test
  public void givenValidAccountId_WhenGetByAccountId_ThenReturn200_Success() throws Exception {

    // Given
    final String accountId = "1";

    given(transactionService.findAllTransactionsByAccountId(1L))
        .willAnswer(
            invocation -> {
              final List<Transaction> transactions = new ArrayList<>();
              final Transaction transaction1 = getTransaction();
              final Transaction transaction2 = getTransaction();

              transaction1.setId(1L);
              transaction2.setId(2L);
              transaction2.setAmount(20.0);
              transactions.add(transaction1);
              transactions.add(transaction2);

              return transactions;
            });

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(TRANSACTION_URI).param("accountId", accountId))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$", hasSize(2)));

    verify(transactionService, times(1)).findAllTransactionsByAccountId(1L);
  }

  @Test
  public void givenValidAccountIdWithNoTransaction_WhenGetByAccountId_ThenReturn200_EmptyList()
      throws Exception {

    // Given
    final String accountId = "1";

    given(transactionService.findAllTransactionsByAccountId(1L))
        .willAnswer(invocation -> Collections.emptyList());

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(TRANSACTION_URI).param("accountId", accountId))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$", hasSize(0)));

    verify(transactionService, times(1)).findAllTransactionsByAccountId(1L);
  }

  @Test
  public void givenNullAccountId_WhenGetByAccountId_ThenReturn400_BadRequest() throws Exception {
    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(TRANSACTION_URI).param("accountId", ""))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(containsString("Required Long parameter 'accountId' is not present")));
  }

  @Test
  public void givenInvalidAccountId_WhenGetByAccountId_ThenReturn400_BadRequest() throws Exception {
    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.get(TRANSACTION_URI).param("accountId", "0"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    containsString(
                        "TransactionController#getByAccountId.accountId: must be greater than or equal to 1")));
  }

  @Test
  public void givenValidTransactionId_WhenDeleteById_ThenReturn200_Success() throws Exception {
    // Given
    final String transactionId = "1";

    willDoNothing().given(transactionService).deleteTransaction(1L);

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.delete(TRANSACTION_URI + "/{id}", transactionId))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    verify(transactionService, times(1)).deleteTransaction(1L);
  }

  @Test
  public void givenValidTransactionId_WhenDeleteById_ThenReturn404_NotFound() throws Exception {
    // Given
    final String transactionId = "1";

    willThrow(TransactionNotFoundException.class).given(transactionService).deleteTransaction(1L);

    // Then
    mockMvc
        .perform(MockMvcRequestBuilders.delete(TRANSACTION_URI + "/{id}", transactionId))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(status().reason("Unable to find any transactions with the given id"));

    verify(transactionService, times(1)).deleteTransaction(1L);
  }
}
