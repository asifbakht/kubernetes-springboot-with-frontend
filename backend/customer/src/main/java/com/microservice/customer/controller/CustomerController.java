package com.microservice.customer.controller;

import com.microservice.customer.dto.CustomerDTO;
import com.microservice.customer.dto.Response;
import com.microservice.customer.dto.ResponsePager;
import com.microservice.customer.exception.DuplicateException;
import com.microservice.customer.exception.GenericException;
import com.microservice.customer.exception.NotFoundException;
import com.microservice.customer.service.CustomerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.microservice.customer.utils.Constants.CACHE_CUSTOMER;
import static com.microservice.customer.utils.Constants.CUSTOMER_SERVICE;
import static com.microservice.customer.utils.Constants.REQUIRE_ID;
import static com.microservice.customer.utils.Constants.SUCCESS_DELETE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * apis related to customer
 *
 * @author Asif Bakht
 * @since 2024
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "customer", produces = APPLICATION_JSON_VALUE)
public class CustomerController {
    private final CustomerService customerService;

    /**
     * add customer api
     *
     * @param customerDTO {@link CustomerDTO} customer dto request body
     * @return {@link CustomerDTO} customer dto with id populated
     */
    @Operation(summary = "add customer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            examples = {
                                    @ExampleObject(name = "add customer",
                                            summary = "Adding customer and returns error if email is not present",
                                            value = """
                                                        {
                                                            "statusCode": 400,
                                                            "content": "email is required"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "500",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "add customer",
                                            summary = "When adding customer and occurs unexpected error",
                                            value = """
                                                        {
                                                            "statusCode": 500,
                                                            "content": "Unknown error occurred please try again later"
                                                        }
                                                    """
                                    )
                            }))
    })
    @PostMapping
    @CircuitBreaker(name = CUSTOMER_SERVICE, fallbackMethod = "serviceUnavailable")
    public ResponseEntity<Response<?>> addCustomer(@Valid @RequestBody final CustomerDTO customerDTO) {
        try {
            log.info("Add customer api initiated");
            final CustomerDTO responseDTO = customerService.add(customerDTO);
            log.info("Add customer api completed");
            return ResponseEntity
                    .status(CREATED)
                    .body(new Response<>(responseDTO, CREATED.value()));
        } catch (final GenericException | IllegalArgumentException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new Response<>(e.getMessage(), BAD_REQUEST.value()));
        }
    }


    /**
     * update customer record api
     *
     * @param id          {@link String} customer id
     * @param customerDTO {@link CustomerDTO} updated customer dto object
     */
    @Operation(summary = "update customer's information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "updateCustomer",
                                            summary = "Updating customer and returns error phoneNumber is not present",
                                            value = """
                                                        {
                                                            "statusCode": 400,
                                                            "content": "phoneNumber is required"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(
                                            name = "updateCustomer",
                                            summary = "Updating customer that does not exists",
                                            value = """
                                                        {
                                                            "statusCode": 404,
                                                            "content": "Customer not found"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "409",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(
                                            name = "updateCustomer",
                                            summary = "Updating customer that already exists returns conflict error",
                                            value = """
                                                        {
                                                            "statusCode": 409,
                                                            "content": "Customer with same details already exists"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "500",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "updateCustomer",
                                            summary = "When updating customer and occurs unexpected error",
                                            value = """
                                                        {
                                                            "statusCode": 500,
                                                            "content": "Unknown error occurred please try again later"
                                                        }
                                                    """
                                    )
                            }))
    })
    @PutMapping("/{id}")
    @CachePut(value = CACHE_CUSTOMER, key = "#id")
    @CircuitBreaker(name = CUSTOMER_SERVICE, fallbackMethod = "serviceUnavailable")
    public ResponseEntity<Response<?>> update(@PathVariable("id") final String id,
                                              @Valid @RequestBody final CustomerDTO customerDTO) {

        try {
            log.info("Update customer api initiated: {}", id);
            Objects.requireNonNull(id, REQUIRE_ID);
            final CustomerDTO responseDTO = customerService.update(id, customerDTO);
            log.info("Update customer api completed: {}", id);
            return ResponseEntity
                    .status(OK)
                    .body(new Response<>(responseDTO, OK.value()));
        } catch (final GenericException | IllegalArgumentException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new Response<>(e.getMessage(), BAD_REQUEST.value()));
        } catch (final DuplicateException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(CONFLICT)
                    .body(new Response<>(e.getMessage(), CONFLICT.value()));
        } catch (final NotFoundException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new Response<>(e.getMessage(), NOT_FOUND.value()));
        }
    }

    /**
     * retrieve customer information api
     *
     * @param id {@link String} customer id
     * @return {@link CustomerDTO} customer dto object
     */
    @Operation(summary = "get customer's information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "getCustomer",
                                            summary = "Get customer and returns error if id is not provided",
                                            value = """
                                                        {
                                                            "statusCode": 400,
                                                            "content": "id is required"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(
                                            name = "getCustomer",
                                            summary = "Get customer that does not exists",
                                            value = """
                                                        {
                                                            "statusCode": 404,
                                                            "content": "Payment not found"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "500",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "getCustomer",
                                            summary = "When retrieving customer and unexpected error occurs",
                                            value = """
                                                        {
                                                            "statusCode": 500,
                                                            "content": "Unknown error occurred please try again later"
                                                        }
                                                    """
                                    )
                            }))
    })
    @GetMapping("/{id}")
    @CachePut(value = CACHE_CUSTOMER, key = "#id")
    @CircuitBreaker(name = CUSTOMER_SERVICE, fallbackMethod = "serviceUnavailable")
    public ResponseEntity<Response<?>> get(@PathVariable("id") final String id) {
        try {
            log.info("Get customer initiated: {}", id);
            Objects.requireNonNull(id, REQUIRE_ID);
            final CustomerDTO responseDTO = customerService.get(id);
            log.info("Get customer completed: {}", id);
            return ResponseEntity
                    .status(OK)
                    .body(new Response<>(responseDTO, OK.value()));
        } catch (final GenericException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new Response<>(e.getMessage(), BAD_REQUEST.value()));
        } catch (final NotFoundException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new Response<>(e.getMessage(), NOT_FOUND.value()));
        }
    }

    /**
     * @return {@link Response} response with status
     */
    @Operation(summary = "delete customer's information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "deleteCustomer",
                                            value = """
                                                        {
                                                            "statusCode": 200,
                                                            "content": "Customer deleted"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "deleteCustomer",
                                            value = """
                                                        {
                                                            "statusCode": 400,
                                                            "content": "id is required"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(
                                            name = "deleteCustomer",
                                            value = """
                                                        {
                                                            "statusCode": 404,
                                                            "content": "Customer not found"
                                                        }
                                                    """
                                    )
                            })),
            @ApiResponse(responseCode = "500",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "deleteCustomer",
                                            value = """
                                                        {
                                                            "statusCode": 500,
                                                            "content": "Unknown error occurred please try again later"
                                                        }
                                                    """
                                    )
                            }))
    })
    @DeleteMapping("/{id}")
    @CacheEvict(value = CACHE_CUSTOMER, key = "#id", allEntries = true)
    @CircuitBreaker(name = CUSTOMER_SERVICE, fallbackMethod = "serviceUnavailable")
    public ResponseEntity<Response<?>> delete(@PathVariable("id") final String id) {
        try {
            log.info("Delete customer initiated: {}", id);
            Objects.requireNonNull(id, REQUIRE_ID);
            customerService.delete(id);
            log.info("Delete customer completed: {}", id);
            return ResponseEntity
                    .status(OK)
                    .body(new Response<>(SUCCESS_DELETE, OK.value()));
        } catch (final GenericException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new Response<>(e.getMessage(), BAD_REQUEST.value()));
        } catch (final NotFoundException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new Response<>(e.getMessage(), NOT_FOUND.value()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllCustomers(final Pageable pageRequest) {
        try {
            log.info("Search customer with pagination initiated: {}", pageRequest);
            final Page<CustomerDTO> pageCustomers = customerService.getAll(pageRequest);
            final ResponsePager<?> responsePage = new ResponsePager<>(pageCustomers.getContent(),
                    pageCustomers.getNumber(),
                    pageCustomers.getTotalElements(),
                    pageCustomers.getTotalPages()
            );
            log.info("Search customer with pagination completed");
            return ResponseEntity
                    .status(OK)
                    .body(responsePage);
        } catch (final GenericException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(new Response<>(e.getMessage(), BAD_REQUEST.value()));
        } catch (final NotFoundException e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity
                    .status(NOT_FOUND)
                    .body(new Response<>(e.getMessage(), NOT_FOUND.value()));
        }
    }

    /**
     * circuit breaker to avoid error calls for adding customer
     *
     * @param customerDTO {@link CustomerDTO} customer payload
     * @param e           {@link Throwable} unexpected exception
     * @return {@link ResponseEntity} response entity with 503 error code
     * @throws Throwable {@link Throwable} exception during process
     */
    private ResponseEntity<Response<?>> serviceUnavailable(final CustomerDTO customerDTO,
                                                           final Throwable e) throws Throwable {
        log.error("Could not process customer, email: {}, error: {}",
                customerDTO.getEmail(), e.getMessage());
        throw e;
    }

    /**
     * circuit breaker to avoid error calls for updating customer
     *
     * @param id          {@link String} customer id
     * @param customerDTO {@link CustomerDTO} customer payload
     * @param e           {@link Throwable} unexpected exception
     * @return {@link ResponseEntity} response entity with 503 error code
     * @throws Throwable {@link Throwable} exception during process
     */
    private ResponseEntity<Response<?>> serviceUnavailable(final String id,
                                                           final CustomerDTO customerDTO,
                                                           final Throwable e) throws Throwable {
        log.error("Could not process customer, id: {}, error: {}", id, e.getMessage());
        throw e;
    }

    /**
     * circuit breaker to avoid error calls for cancelling/deleting customer
     *
     * @param id {@link String} customer id
     * @param e  {@link Throwable} unexpected exception
     * @return {@link ResponseEntity} response entity with 503 error code
     * @throws Throwable {@link Throwable} exception during process
     */
    private ResponseEntity<Response<?>> serviceUnavailable(final String id,
                                                           final Throwable e) throws Throwable {
        log.error("Could not process customer, id: {}, error: {}", id, e.getMessage());
        throw e;
    }

}
