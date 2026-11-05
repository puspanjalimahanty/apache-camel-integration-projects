package com.example.edifileprocessing.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.example.edifileprocessing.bean.PurchaseOrderValidationBean;
import com.example.edifileprocessing.exception.DuplicatePurchaseOrderException;
import com.example.edifileprocessing.processor.CorrelationIdProcessor;
import com.example.edifileprocessing.processor.EdiParserProcessor;
import com.example.edifileprocessing.service.PurchaseOrderService;

@Component
public class EdiFileRoute extends RouteBuilder {

    private final EdiParserProcessor ediParserProcessor;
    private final PurchaseOrderValidationBean validationBean;
    private final PurchaseOrderService purchaseOrderService;
    private final CorrelationIdProcessor correlationIdProcessor;

    public EdiFileRoute(
            EdiParserProcessor ediParserProcessor,
            PurchaseOrderValidationBean validationBean,
            PurchaseOrderService purchaseOrderService,
            CorrelationIdProcessor correlationIdProcessor) {

        this.ediParserProcessor = ediParserProcessor;
        this.validationBean = validationBean;
        this.purchaseOrderService = purchaseOrderService;
        this.correlationIdProcessor = correlationIdProcessor;
    }

    @Override
    public void configure() {
    	
    	onException(DuplicatePurchaseOrderException.class)
        .maximumRedeliveries(0)
        .handled(true)
        .log("Duplicate EDI purchase order rejected: ${exception.message}");
    	
    	 onException(Exception.class)
         .maximumRedeliveries(3)
         .redeliveryDelay(2000)
         .handled(false);

        from("file:{{app.edi.input-directory}}"
                + "?include=.*\\.edi"
                + "&move={{app.edi.processed-directory}}/${file:name}"
                + "&moveFailed={{app.edi.error-directory}}/${file:name}")
            .routeId("edi-file-polling-route")
            
            .process(correlationIdProcessor)

            .log("EDI file received: ${file:name}, Correlation ID:${header.CorrelationId}")

            .convertBodyTo(String.class)

            .process(ediParserProcessor)
            .bean(validationBean, "validate")
            .bean(purchaseOrderService, "save")
            
            .log("Purchase order saved successfully");
    }
}