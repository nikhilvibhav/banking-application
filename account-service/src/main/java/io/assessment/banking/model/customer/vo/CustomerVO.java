package io.assessment.banking.model.customer.vo;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.assessment.banking.model.account.vo.AccountVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Models the Customer Data Transfer Object
 *
 * @author Nikhil Vibhav
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerVO {
  private Long id;
  private String firstName;
  private String surname;
  private String email;
  private List<AccountVO> accounts;
  private ZonedDateTime dateCreated;
  private ZonedDateTime dateUpdated;
}
