package io.assessment.banking.transaction.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.log4j.Log4j2;

/**
 * REST Controller Advice for handling exception scenarios
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
   * @return the {@link ResponseEntity} containing the error response
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
   * @param headers - the HTTP headers
   * @param status - the HTTP status
   * @param request - the incoming request
   * @return the {@link ResponseEntity} containing the error response @param headers
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
   * Handles the {@link MissingServletRequestParameterException}
   *
   * @param ex - instance of {@link MissingServletRequestParameterException}
   * @param headers - the HTTP headers
   * @param status - the HTTP status
   * @param request - the incoming request
   * @return the {@link ResponseEntity} containing the error response
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      final MissingServletRequestParameterException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    log.info("Handling MissingServletRequestParameterException - {}", ex.getMessage());

    final List<String> errors = Collections.singletonList(ex.getMessage());
    final Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("message", "The request failed validation checks. Error Count - " + errors.size());
    body.put("errors", errors);

    return new ResponseEntity<>(body, headers, status);
  }
}
