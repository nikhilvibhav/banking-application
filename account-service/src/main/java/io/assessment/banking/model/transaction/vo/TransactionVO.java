package io.assessment.banking.model.transaction.vo;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.assessment.banking.constant.transaction.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Models the request to create a transaction
 *
 * @author Nikhil Vibhav
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionVO {

  private Long id;
  private Double amount;
  private TransactionType type;
  private Long accountId;
  private ZonedDateTime dateTransacted;
}
