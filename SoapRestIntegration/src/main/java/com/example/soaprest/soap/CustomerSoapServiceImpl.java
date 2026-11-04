package com.example.soaprest.soap;

import jakarta.jws.WebService;

@WebService(
    endpointInterface = "com.example.soaprest.soap.CustomerSoapService",
    serviceName = "CustomerSoapService",
    targetNamespace = "http://soaprest.example.com/customer"
)
public class CustomerSoapServiceImpl implements CustomerSoapService {

    @Override
    public CustomerSoapResponse getCustomer(String customerId) {

        if ("CUST-101".equals(customerId)) {
            return new CustomerSoapResponse(
                "CUST-101",
                "John Smith",
                "john@example.com"
            );
        }

        if ("CUST-102".equals(customerId)) {
            return new CustomerSoapResponse(
                "CUST-102",
                "Alice Johnson",
                "alice@example.com"
            );
        }

        return new CustomerSoapResponse(
            customerId,
            "Customer Not Found",
            "Not Available"
        );
    }
}
