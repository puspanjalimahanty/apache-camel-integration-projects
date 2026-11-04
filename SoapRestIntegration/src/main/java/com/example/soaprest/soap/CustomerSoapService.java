package com.example.soaprest.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(
    name = "CustomerSoapService",
    targetNamespace = "http://soaprest.example.com/customer"
)
public interface CustomerSoapService {

    @WebMethod
    CustomerSoapResponse getCustomer(
        @WebParam(name = "customerId") String customerId
    );
}