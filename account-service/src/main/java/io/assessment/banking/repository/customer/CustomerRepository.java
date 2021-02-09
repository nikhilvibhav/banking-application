package io.assessment.banking.repository.customer;

import org.springframework.data.repository.CrudRepository;

import io.assessment.banking.model.customer.entity.Customer;

/**
 * Spring JPA repository for {@link Customer} entity
 *
 * @author Nikhil Vibhav
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {}
