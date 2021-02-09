package io.assessment.banking.service.transaction.impl;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import io.assessment.banking.exception.transaction.TransactionServiceInvalidResponseException;
import io.assessment.banking.exception.transaction.TransactionServiceRestException;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.assessment.banking.service.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the {@link TransactionService}
 *
 * @author Nikhil Vibhav
 */
@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {

  private final RestTemplate restTemplate;
  private final Gson gson;

  @Value("${transaction.service.base.resource.path:http://localhost:8081/api/bank/v1/transaction}")
  private String transactionServiceBaseUrl;

  @Autowired
  public TransactionServiceImpl(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    gson =
        new GsonBuilder()
            .registerTypeAdapter(
                ZonedDateTime.class,
                (JsonDeserializer<ZonedDateTime>)
                    (json, type, jsonDeserializationContext) ->
                        ZonedDateTime.from(
                            ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString())
                                .toLocalDateTime()
                                .atZone(ZoneId.systemDefault())))
            .create();
  }

  /**
   * Calls Transaction Service's API to save a transaction
   *
   * @param transaction - the transaction to save
   * @return the saved {@link TransactionVO}
   * @throws TransactionServiceRestException - when a REST error occurs while calling the
   *     transaction service
   * @throws TransactionServiceInvalidResponseException - when the transaction service returns a
   *     blank or non-2xx response
   */
  @Override
  public TransactionVO saveTransaction(final TransactionVO transaction)
      throws TransactionServiceRestException, TransactionServiceInvalidResponseException {

    final HttpEntity<TransactionVO> request = new HttpEntity<>(transaction);

    try {
      final ResponseEntity<TransactionVO> responseEntity =
          restTemplate.exchange(
              transactionServiceBaseUrl, HttpMethod.PUT, request, TransactionVO.class);

      if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
        throw new TransactionServiceInvalidResponseException(
            "The Transaction Service's Save Transaction API returned an invalid response");
      }

      return responseEntity.getBody();

    } catch (RestClientException | JsonParseException ex) {
      log.error(
          "Error occurred while calling {} for saving the transaction {}",
          transactionServiceBaseUrl,
          transaction);
      throw new TransactionServiceRestException("REST call to save the transaction failed");
    }
  }

  /**
   * Calls Transaction service to get the transactions by accountId
   *
   * @param accountId - the accountId for which to query the transactions
   * @return {@link List} of {@link TransactionVO} for the account id
   * @throws TransactionServiceRestException - when a REST error occurs while calling the
   *     transaction service
   * @throws TransactionServiceInvalidResponseException - when the transaction service returns a
   *     blank or non-2xx response
   */
  @Override
  public List<TransactionVO> getAllTransactions(final Long accountId)
      throws TransactionServiceRestException, TransactionServiceInvalidResponseException {

    final URI uri =
        UriComponentsBuilder.fromUriString(transactionServiceBaseUrl)
            .queryParam("accountId", accountId)
            .build()
            .toUri();

    try {
      final ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

      if (!responseEntity.getStatusCode().is2xxSuccessful()
          || StringUtils.isBlank(responseEntity.getBody())) {
        throw new TransactionServiceInvalidResponseException(
            "The Transaction Service's Get Transaction by AccountId API returned an invalid response");
      }

      return gson.fromJson(
          responseEntity.getBody(), new TypeToken<List<TransactionVO>>() {}.getType());

    } catch (RestClientException | JsonParseException ex) {
      log.error(
          "Error occurred while calling - {} to get the transactions for accountId - {}",
          uri.toString(),
          accountId);
      throw new TransactionServiceRestException(
          "REST call to get the transactions by accountId failed");
    }
  }

  /**
   * Calls transaction service to delete the transaction by the given transaction id
   *
   * @param id - the given transaction id
   * @throws TransactionServiceRestException - when a REST error occurs while calling the
   *     transaction service
   */
  @Override
  public void deleteTransaction(final Long id) throws TransactionServiceRestException {

    try {
      restTemplate.delete(transactionServiceBaseUrl + "/" + id);
    } catch (RestClientException ex) {
      log.error(
          "Error occurred while calling Transaction Service's DELETE api to delete the transactions by id - {}",
          id);
      throw new TransactionServiceRestException("REST call to delete the transaction by id failed");
    }
  }
}
