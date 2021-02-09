package io.assessment.banking.transaction.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import io.assessment.banking.transaction.model.entity.Transaction;

/**
 * JPA Repository for interacting with {@link Transaction} table in the db
 *
 * @author Nikhil Vibhav
 */
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

  List<Transaction> findAllByAccountId(final Long accountId);
}
