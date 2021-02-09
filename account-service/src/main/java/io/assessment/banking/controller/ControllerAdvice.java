package io.assessment.banking.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.assessment.banking.controller.account.AccountController;
import io.assessment.banking.exception.account.CreditTooLowException;
import io.assessment.banking.model.error.ValidationErrorResponse;
import lombok.extern.log4j.Log4j2;

/**
 * Controller Advice for exception handling for {@link AccountController}
 *
 * @author Nikhil Vibhav
 */
@RestControllerAdvice
@Log4j2
public class ControllerAdvice extends ResponseEntityExceptionHandler {

  /**
   * Handles the {@link ConstraintViolationException}
   *
   * @param ex - instance of {@link ConstraintViolationException}
   * @return the {@link ValidationErrorResponse}
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex) {

    log.info("Handling ConstraintViolationException - {}", ex.getMessage());

    final List<String> errors =
        ex.getConstraintViolations().stream()
            .map(
                violation ->
                    violation.getRootBeanClass().getSimpleName()
                        + "#"
                        + violation.getPropertyPath()
                        + ": "
                        + violation.getMessage())
            .collect(Collectors.toList());

    final Map<String, Object> body = new HashMap<>();

    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("message", "The request failed validation checks. Error count - " + errors.size());
    body.put("errors", errors);

    return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles the {@link MethodArgumentNotValidException}
   *
   * @param ex - instance of {@link MethodArgumentNotValidException}
   * @return the {@link ValidationErrorResponse}
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {

    log.info("Handling MethodArgumentNotValidException - {}", ex.getMessage());

    final List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

    final Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("message", "The request failed validation checks. Error count - " + errors.size());
    body.put("errors", errors);

    return new ResponseEntity<>(body, headers, status);
  }

  /**
   * Handles the {@link CreditTooLowException}
   *
   * @param ex - instance of {@link CreditTooLowException}
   * @return the {@link ValidationErrorResponse}
   */
  @ExceptionHandler(CreditTooLowException.class)
  public ResponseEntity<Object> handleCreditTooLow(final CreditTooLowException ex) {

    log.info("Handling CreditTooLowException - {}", ex.getMessage());

    final Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("message", "The request failed validation checks. Error count - 1");
    body.put("errors", Collections.singletonList(ex.getMessage()));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
