package com.example.soaprest.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.soaprest.soap.CustomerSoapServiceImpl;

@Configuration
public class SoapConfig {

    @Bean
    public EndpointImpl customerSoapEndpoint(Bus bus) {
        EndpointImpl endpoint =
                new EndpointImpl(bus, new CustomerSoapServiceImpl());

        endpoint.publish("/customer");

        return endpoint;
    }
}