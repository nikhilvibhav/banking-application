package io.assessment.banking.model.request;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for modelling the request for create bank account request
 *
 * @author Nikhil Vibhav
 */
@Data
@NoArgsConstructor
public class CreateBankAccount implements Serializable {

  private static final long serialVersionUID = 42L;

  @NotNull(message = "customerId cannot be null")
  private Long customerId;

  @NotNull(message = "initialAmount cannot be null")
  @Min(1)
  private Double initialAmount;
}
