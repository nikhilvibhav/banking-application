package io.assessment.banking.repository.account;

import org.springframework.data.repository.CrudRepository;

import io.assessment.banking.model.account.entity.Account;

/**
 * Spring Data JPA repository for {@link Account} entity
 *
 * @author Nikhil Vibhav
 */
public interface AccountRepository extends CrudRepository<Account, Long> {}
