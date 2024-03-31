package com.microservice.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Value
@EqualsAndHashCode
public class CustomerDTO {
    private String id;
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @NotBlank(message = "email is required")
    @Email(message = "required correct email")
    private String email;
    @NotBlank(message = "birth date is required")
    private String dateOfBirth;
    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
    @NotBlank(message = "itin/ssn is required")
    private String itinOrSsn;
}
