package io.assessment.banking.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Models the violations that happens when validating the request
 *
 * @author Nikhil Vibhav
 */
@Data
@AllArgsConstructor
public class Violation {
  private String message;
}
