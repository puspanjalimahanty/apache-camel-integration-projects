package com.example.dataenrichment.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.dataenrichment.model.Customer;
import com.example.dataenrichment.model.Product;
import com.example.dataenrichment.repository.CustomerRepository;
import com.example.dataenrichment.repository.ProductRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        return args -> {

            // Insert sample customers
            if (!customerRepository.existsById("C101")) {
                Customer customer = new Customer();
                customer.setCustomerId("C101");
                customer.setCustomerName("Rahul");
                customer.setCustomerEmail("rahul@gmail.com");
                customer.setCustomerAddress("Vizag");

                customerRepository.save(customer);
            }

            if (!customerRepository.existsById("C102")) {
                Customer customer = new Customer();
                customer.setCustomerId("C102");
                customer.setCustomerName("Priya");
                customer.setCustomerEmail("priya@gmail.com");
                customer.setCustomerAddress("Hyderabad");

                customerRepository.save(customer);
            }

            // Insert sample products
            if (!productRepository.existsById("P101")) {
                Product product = new Product();
                product.setProductCode("P101");
                product.setProductName("Laptop");
                product.setUnitPrice(50000);
                product.setAvailable(true);

                productRepository.save(product);
            }

            if (!productRepository.existsById("P102")) {
                Product product = new Product();
                product.setProductCode("P102");
                product.setProductName("Mobile");
                product.setUnitPrice(20000);
                product.setAvailable(true);

                productRepository.save(product);
            }

            System.out.println("Sample customer and product data initialized.");
        };
    }
}