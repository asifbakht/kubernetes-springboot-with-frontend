package com.microservice.customer.utils;

import com.microservice.customer.exception.NoInstanceException;

/**
 * all static field resides here that will be accessible
 * accross application
 *
 * @author Asif Bakht
 * @since 2024
 */
public final class Constants {

    private Constants() throws NoInstanceException {
        throw new NoInstanceException("Object creation of this class is not allowed");
    }

    public static final String DELIMETER_COMMA = ",";

    /**
     * static variables across application
     */

    public static final String CACHE_CUSTOMER = "customer";
    public static final String CUSTOMER_SERVICE = "customer-service";

    public static final String REQUIRE_ID = "id cannot be null";

    public static final String NOT_FOUND = "customer not found";

    public static final String RESOURCE_ALREADY_EXISTS = "customer with same details already exists";

    public static final String SUCCESS_DELETE = "Customer is deleted";

}
