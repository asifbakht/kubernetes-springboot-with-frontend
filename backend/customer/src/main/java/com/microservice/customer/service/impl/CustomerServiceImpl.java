package com.microservice.customer.service.impl;

import com.microservice.customer.dto.CustomerDTO;
import com.microservice.customer.entity.Customer;
import com.microservice.customer.exception.DuplicateException;
import com.microservice.customer.exception.NotFoundException;
import com.microservice.customer.mapper.CustomerMapper;
import com.microservice.customer.repository.CustomerRepository;
import com.microservice.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.microservice.customer.utils.Constants.NOT_FOUND;
import static com.microservice.customer.utils.Constants.RESOURCE_ALREADY_EXISTS;

/**
 * all customer crud related business logic resides here
 *
 * @author Asif Bakht
 * @since 2024
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * before adding customer it fetches existing customer
     * record by email and return back that information
     * else it will then crate a new customer and save
     * that information in db
     *
     * @param customerDTO {@link CustomerDTO} customer payload
     * @return {@link CustomerDTO} customer payload with id populated
     */
    @Override
    public CustomerDTO add(CustomerDTO customerDTO) {
        log.info("Customer add function started");
        log.debug("Customer information: {}", customerDTO);
        final Customer customer = customerRepository
                .findByEmail(customerDTO.getEmail())
                .orElseGet(() -> customerRepository
                        .save(customerMapper.toEntity(customerDTO)));
        log.info("Customer add function completed");
        return customerMapper.toDTO(customer);
    }

    /**
     * fetch customer information if found then it will update
     * it or else it will throw exception
     *
     * @param id          {@link String} customer id
     * @param customerDTO {@link CustomerDTO} updated customer payload detail
     * @return {@link CustomerDTO} updated customer payload
     */
    @Override
    public CustomerDTO update(final String id, CustomerDTO customerDTO) {
        customerRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        if (!id.equalsIgnoreCase(customerDTO.getId()))
            throw new DuplicateException(RESOURCE_ALREADY_EXISTS);
        final Customer customer = customerMapper.toEntity(customerDTO);
        customer.setId(id);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    /**
     * fetch customer details from database if not found
     * then throws exception
     *
     * @param id {@link String} customer id
     * @return {@link CustomerDTO} customer dto payload
     */
    @Override
    public CustomerDTO get(final String id) {
        final Customer customer = customerRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        return customerMapper.toDTO(customer);
    }

    /**
     * delete customer from database if it does not exists
     * then throws exception
     *
     * @param id {@link String} customer id
     */
    @Override
    public void delete(final String id) {
        customerRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        customerRepository.deleteById(id);
    }

    /**
     * return all customer's from database with paginated properties
     *
     * @param pageable   {@link Pageable} paginated properties
     */
    @Override
    public Page<CustomerDTO> getAll(final Pageable pageable) {
        return customerRepository
                .findAll(pageable)
                .map(customerMapper::toDTO);
    }

}
