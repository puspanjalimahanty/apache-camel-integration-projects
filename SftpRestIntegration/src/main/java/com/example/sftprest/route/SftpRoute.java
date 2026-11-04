package com.example.sftprest.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.sftprest.service.OrderFileValidator;

@Component
public class SftpRoute extends RouteBuilder {
	

    @Value("${app.sftp.host}")
    private String host;

    @Value("${app.sftp.port}")
    private int port;

    @Value("${app.sftp.username}")
    private String username;

    @Value("${app.sftp.password}")
    private String password;

    @Value("${app.sftp.directory}")
    private String directory;
    
    @Autowired
    private OrderFileValidator orderFileValidator;

    @Override
    public void configure() {
    	
    	onException(Exception.class)
        .maximumRedeliveries(3)
        .redeliveryDelay(2000)
        .retryAttemptedLogLevel(LoggingLevel.WARN)
        .handled(false);

        from("sftp://" + username + "@" + host + ":" + port + directory
                + "?password=" + password
                + "&delay=5000"
                + "&readLock=changed"
                + "&strictHostKeyChecking=no"
                + "&move=.processed/${file:name}"
                + "&moveFailed=.error/${file:name}")
            .routeId("sftp-to-rest-route")
            .process(exchange -> {
                String correlationId = java.util.UUID.randomUUID().toString();
                exchange.getIn().setHeader("CorrelationId", correlationId);
            })
            
            .idempotentConsumer(
            	    header("CamelFileName"),
            	    MemoryIdempotentRepository.memoryIdempotentRepository(1000)
            	)
            
            .log("[$ {header.CorrelationId}] Received SFTP file: ${header.CamelFileName}")

            .log("File received from SFTP: ${header.CamelFileName}")
            .log("Original file content: ${body}")

            // Convert CSV row into JSON
            .process(exchange -> {
                String content = exchange.getIn().getBody(String.class);
                orderFileValidator.validate(content);

                String[] fields = content.trim().split(",");

                if (fields.length != 5) {
                    throw new IllegalArgumentException(
                            "Invalid order file format. Expected 5 fields.");
                }

                String json = """
                    {
                      "orderId": "%s",
                      "customerName": "%s",
                      "productName": "%s",
                      "quantity": %s,
                      "unitPrice": %s
                    }
                    """.formatted(
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim(),
                        fields[4].trim()
                    );

                exchange.getIn().setBody(json);
            })

            .setHeader("Content-Type", constant("application/json"))
            .setHeader("CamelHttpMethod", constant("POST"))
            
            .setHeader("X-Correlation-ID", header("CorrelationId"))

            .to("http://localhost:8085/api/orders")

            .log("REST response received: ${body}");
    }
}
