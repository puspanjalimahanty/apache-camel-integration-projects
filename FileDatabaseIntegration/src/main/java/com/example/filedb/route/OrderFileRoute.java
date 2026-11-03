package com.example.filedb.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

import com.example.filedb.dto.OrderFileRecord;
import com.example.filedb.service.OrderService;

@Component
public class OrderFileRoute extends RouteBuilder {

    private final OrderService orderService;

    public OrderFileRoute(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void configure() {

        onException(Exception.class)
            .handled(false)
            .log("File processing failed: ${header.CamelFileName}")
            .log("Failure reason: ${exception.message}");

        from("file:C:/CamelProject/FileDatabaseIntegration/data/orders/incoming"
                + "?include=.*\\.csv"
                + "&move=C:/CamelProject/FileDatabaseIntegration/data/orders/processed/${file:name}"
                + "&moveFailed=C:/CamelProject/FileDatabaseIntegration/data/orders/error/${file:name}")
            .routeId("order-file-database-route")

            .idempotentConsumer(
                header(Exchange.FILE_NAME),
                MemoryIdempotentRepository.memoryIdempotentRepository(1000)
            )
            .log("Started processing file: ${header.CamelFileName}")

            // Keep the existing CSV unmarshalling and database processing here

            .unmarshal()
                .bindy(BindyType.Csv, OrderFileRecord.class)

            .split(body())
                .stopOnException()
                .process(exchange -> {

                    OrderFileRecord record =
                            exchange.getIn().getBody(OrderFileRecord.class);

                    String sourceFile = exchange.getIn()
                            .getHeader(Exchange.FILE_NAME, String.class);

                    orderService.saveOrder(record, sourceFile);
                })
                .log("Successfully saved order: ${body.orderId}")
            .end()

            .log("Successfully completed file: ${header.CamelFileName}");
    }
}