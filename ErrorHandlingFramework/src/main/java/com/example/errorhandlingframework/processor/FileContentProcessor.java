package com.example.errorhandlingframework.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.exception.FileParsingException;
import com.example.errorhandlingframework.model.Order;

@Component
public class FileContentProcessor implements Processor {

    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "orderId", "customerName", "productCode",
            "quantity", "unitPrice"
    );

    @Override
    public void process(Exchange exchange) {

        String content = exchange.getMessage().getBody(String.class);

        if (content == null || content.isBlank()) {
            throw new FileParsingException("File is empty");
        }

        List<Order> orders = new ArrayList<>();

        try (CSVParser parser = CSVParser.parse(
                content,
                CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build())) {

            if (!parser.getHeaderMap().keySet()
                    .containsAll(REQUIRED_HEADERS)) {
                throw new FileParsingException(
                        "CSV file is missing required headers");
            }

            for (CSVRecord record : parser) {

                int rowNumber = (int) record.getRecordNumber() + 1;

                try {
                    String orderId = record.get("orderId");
                    String customerName = record.get("customerName");
                    String productCode = record.get("productCode");

                    Integer quantity = parseQuantity(
                            record.get("quantity"));

                    BigDecimal unitPrice = parsePrice(
                            record.get("unitPrice"));

                    Order order = new Order(
                            orderId,
                            customerName,
                            productCode,
                            quantity,
                            unitPrice
                    );

                    orders.add(order);

                } catch (NumberFormatException ex) {
                    throw new FileParsingException(
                            "Invalid numeric value at CSV row "
                                    + rowNumber, ex);
                }
            }

        } catch (IOException ex) {
            throw new FileParsingException(
                    "Unable to read or parse CSV content", ex);
        }

        if (orders.isEmpty()) {
            throw new FileParsingException(
                    "CSV file contains no order records");
        }

        exchange.getMessage().setBody(orders);
    }

    private Integer parseQuantity(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value);
    }

    private BigDecimal parsePrice(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return new BigDecimal(value);
    }
}