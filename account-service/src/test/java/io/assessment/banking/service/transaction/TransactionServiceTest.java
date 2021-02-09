package io.assessment.banking.service.transaction;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import io.assessment.banking.AbstractTest;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.assessment.banking.service.transaction.impl.TransactionServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Junit tests for {@link TransactionService}
 *
 * @author Nikhil Vibhav
 */
@ExtendWith(SpringExtension.class)
@RestClientTest(TransactionServiceImpl.class)
@AutoConfigureWebClient(registerRestTemplate = true)
public class TransactionServiceTest extends AbstractTest {

  @Autowired private TransactionServiceImpl transactionService;
  @Autowired private MockRestServiceServer server;

  @Test
  public void givenValidTransaction_WhenSaveTransaction_ThenSucceed() throws Exception {
    final TransactionVO transaction = getTransactionVO();
    transaction.setId(null);
    transaction.setDateTransacted(null);
    final String json =
        "{\"accountId\":1,\"amount\":10,\"dateTransacted\":\"2021-02-09T00:31:45.7462794+05:30\",\"type\":\"CREDIT\"}";

    server
        .expect(requestTo("http://localhost:8081/api/bank/v1/transaction"))
        .andRespond(
            withStatus(HttpStatus.CREATED).body(json).contentType(MediaType.APPLICATION_JSON));

    final TransactionVO savedTransaction = transactionService.saveTransaction(transaction);
    assertEquals(transaction.getAmount(), savedTransaction.getAmount());
    assertNotNull(savedTransaction.getDateTransacted());
  }

  @Test
  public void givenValidAccountId_WhenGetAllTransactionByAccountId_ThenSucceed() throws Exception {
    final String json =
        "[{\"accountId\":1,\"amount\":10,\"dateTransacted\":\"2021-02-09T00:38:41.441411+05:30\",\"type\":\"CREDIT\"},{\"accountId\":1,\"amount\":20,\"dateTransacted\":\"2021-02-09T00:38:54.269594+05:30\",\"type\":\"DEBIT\"},{\"accountId\":1,\"amount\":40,\"dateTransacted\":\"2021-02-09T00:39:00.700492+05:30\",\"type\":\"DEBIT\"},{\"accountId\":1,\"amount\":4000,\"dateTransacted\":\"2021-02-09T00:39:09.097132+05:30\",\"type\":\"CREDIT\"}]";

    server
        .expect(requestTo("http://localhost:8081/api/bank/v1/transaction?accountId=1"))
        .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

    final List<TransactionVO> transactions = transactionService.getAllTransactions(1L);
    assertTrue(transactions.size() > 0);
  }

  @Test
  public void givenValidTransactionId_WhenDeleteTransaction_ThenSucceed() throws Exception {
    server
        .expect(requestTo("http://localhost:8081/api/bank/v1/transaction/1"))
        .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

    transactionService.deleteTransaction(1L);
  }
}
