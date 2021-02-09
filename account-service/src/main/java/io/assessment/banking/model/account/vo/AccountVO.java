package io.assessment.banking.model.account.vo;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.assessment.banking.constant.account.AccountType;
import io.assessment.banking.model.transaction.vo.TransactionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for modelling the request for create bank account request
 *
 * @author Nikhil Vibhav
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountVO {

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @NotNull(message = "customerId cannot be null")
  @Min(0)
  private Long customerId;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private AccountType type;

  @NotNull(message = "initialCredit cannot be null")
  @DecimalMin(value = "0", message = "initialCredit cannot be negative")
  private Double initialCredit;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Double balance;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private List<TransactionVO> transactions;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private ZonedDateTime dateCreated;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private ZonedDateTime dateUpdated;
}
