package io.assessment.banking.model.error;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Error Response model for Validation failures
 *
 * @author Nikhil Vibhav
 */
@Data
public class ValidationErrorResponse {
  private List<Violation> violations = new ArrayList<>();
}
