package com.example.errorhandlingframework.config;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.errorhandlingframework.exception.FileParsingException;
import com.example.errorhandlingframework.exception.InvalidOrderException;
import com.example.errorhandlingframework.exception.TechnicalException;

@Component
public class ErrorHandlingConfig extends RouteBuilder {

    @Override
    public void configure() {

        // 1. File parsing errors: no retry
        onException(FileParsingException.class)
            .maximumRedeliveries(0)
            .handled(false)
            .log(LoggingLevel.ERROR,
                "FILE PARSING ERROR: ${exception.message}");

        // 2. Business validation errors: no retry
        onException(InvalidOrderException.class)
            .maximumRedeliveries(0)
            .handled(false)
            .log(LoggingLevel.ERROR,
                "ORDER VALIDATION ERROR: ${exception.message}");

        // 3. Technical errors: retry, then copy to DLQ
        onException(TechnicalException.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .log(LoggingLevel.ERROR,
                "TECHNICAL ERROR: ${exception.message}")
            .to("file:{{app.file.dead-letter-directory}}"
                + "?fileName=${header.CamelFileName}")
            .handled(false);

        // 4. Unexpected errors: retry, then copy to DLQ
        onException(Exception.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(2000)
            .retryAttemptedLogLevel(LoggingLevel.WARN)
            .log(LoggingLevel.ERROR,
                "UNEXPECTED ERROR: ${exception.message}")
            .to("file:{{app.file.dead-letter-directory}}"
                + "?fileName=${header.CamelFileName}")
            .handled(false);
    }
}
