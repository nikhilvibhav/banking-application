package io.assessment.banking.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.assessment.banking.model.request.CreateBankAccount;

@RestController("/app/bank/v1")
public class BankAccountController {

  @PostMapping(
      value = "/account/current",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> openCurrentAccount(
      @Valid @RequestBody final CreateBankAccount request) {
    return ResponseEntity.created(URI.create("/account/" + request.getCustomerId())).build();
  }
}
