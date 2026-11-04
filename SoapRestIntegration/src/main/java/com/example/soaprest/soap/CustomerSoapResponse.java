package com.example.soaprest.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "CustomerSoapResponse",
    propOrder = {"customerId", "customerName", "email"}
)
public class CustomerSoapResponse {

    private String customerId;
    private String customerName;
    private String email;

    public CustomerSoapResponse() {
    }

    public CustomerSoapResponse(
            String customerId,
            String customerName,
            String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}