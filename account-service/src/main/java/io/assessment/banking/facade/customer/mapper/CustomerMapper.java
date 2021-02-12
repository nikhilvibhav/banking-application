package io.assessment.banking.facade.customer.mapper;

import io.assessment.banking.model.customer.entity.Customer;
import io.assessment.banking.model.customer.vo.CustomerVO;

/**
 * Mapper class to map objects of {@link CustomerVO} to {@link Customer} and vice versa
 *
 * @author Nikhil Vibhav
 */
public class CustomerMapper {

  /**
   * Maps an object of {@link Customer} to {@link CustomerVO}
   *
   * @param customer - the object to map
   * @return the target {@link CustomerVO} object
   */
  public static CustomerVO toCustomerVO(final Customer customer) {
    final CustomerVO customerVO = new CustomerVO();
    customerVO.setId(customer.getId());
    customerVO.setFirstName(customer.getFirstName());
    customerVO.setSurname(customer.getSurname());
    customerVO.setEmail(customer.getEmail());
    customerVO.setDateCreated(customer.getDateCreated());
    customerVO.setDateUpdated(customer.getDateUpdated());
    return customerVO;
  }
}
