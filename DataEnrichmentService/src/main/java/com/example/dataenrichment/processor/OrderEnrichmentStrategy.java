package com.example.dataenrichment.processor;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.example.dataenrichment.dto.ApiErrorResponse;
import com.example.dataenrichment.dto.EnrichedOrderResponse;
import com.example.dataenrichment.dto.OrderRequest;
import com.example.dataenrichment.exception.ResourceNotFoundException;
import com.example.dataenrichment.model.Customer;
import com.example.dataenrichment.model.Product;

@Component
public class OrderEnrichmentStrategy implements AggregationStrategy {


@Override
public Exchange aggregate(Exchange original, Exchange resource) {

    Object resourceBody = resource.getIn().getBody();

    // Check whether product lookup returned an error response
    if (resourceBody instanceof ApiErrorResponse error) {
        throw new ResourceNotFoundException(error.getMessage());
    }

    // Ensure product details are available
    if (!(resourceBody instanceof Product product)) {
        throw new IllegalStateException(
                "Product lookup did not return valid product details");
    }

    OrderRequest order = original.getIn()
            .getBody(OrderRequest.class);

    Customer customer = original.getProperty(
            "customer", Customer.class);

    if (customer == null) {
        throw new IllegalStateException(
                "Customer details are missing during enrichment");
    }

    EnrichedOrderResponse response = new EnrichedOrderResponse();

    response.setOrderId(order.getOrderId());

    response.setCustomerId(customer.getCustomerId());
    response.setCustomerName(customer.getCustomerName());
    response.setCustomerEmail(customer.getCustomerEmail());
    response.setCustomerAddress(customer.getCustomerAddress());

    response.setProductCode(product.getProductCode());
    response.setProductName(product.getProductName());
    response.setUnitPrice(product.getUnitPrice());
    response.setAvailable(product.isAvailable());

    response.setQuantity(order.getQuantity());
    response.setTotalAmount(
            product.getUnitPrice() * order.getQuantity());

    original.getIn().setBody(response);

    return original;
}

}

