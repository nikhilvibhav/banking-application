package io.assessment.banking.transaction.model.vo;

import java.time.ZonedDateTime;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.assessment.banking.transaction.constant.TransactionType;
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

  @NotNull(message = "amount cannot be null")
  @DecimalMin(value = "0", message = "amount cannot be negative")
  private Double amount;

  @NotNull(message = "type cannot be null - it should either be CREDIT or DEBIT")
  private TransactionType type;

  @NotNull(message = "accountId cannot be null")
  private Long accountId;

  private ZonedDateTime dateTransacted;
}
