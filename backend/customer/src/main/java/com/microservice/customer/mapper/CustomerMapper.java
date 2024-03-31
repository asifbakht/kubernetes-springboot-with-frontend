package com.microservice.customer.mapper;

import com.microservice.customer.dto.CustomerDTO;
import com.microservice.customer.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(final CustomerDTO customerDTO);
    CustomerDTO toDTO(final Customer customer);
}
