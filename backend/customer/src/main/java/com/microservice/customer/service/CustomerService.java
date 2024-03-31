package com.microservice.customer.service;

import com.microservice.customer.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    public CustomerDTO add(final CustomerDTO customerDTO);

    public CustomerDTO update(final String id, final CustomerDTO customerDTO);

    public CustomerDTO get(final String id);

    public void delete(final String id);

    public Page<CustomerDTO> getAll(final Pageable pageable);
}
