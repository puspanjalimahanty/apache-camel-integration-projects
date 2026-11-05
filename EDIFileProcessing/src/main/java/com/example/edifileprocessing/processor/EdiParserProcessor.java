package com.example.edifileprocessing.processor;

import java.math.BigDecimal;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.example.edifileprocessing.model.PurchaseOrder;

@Component
public class EdiParserProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        String ediContent = exchange.getIn().getBody(String.class);

        PurchaseOrder order = new PurchaseOrder();

        String[] segments = ediContent.split("~");

        for (String segment : segments) {

            String[] elements = segment.trim().split("\\*");

            if (elements.length == 0) {
                continue;
            }

            switch (elements[0]) {

                case "BEG":
                    // BEG*00*SA*PO12345**20261105
                    if (elements.length > 3) {
                        order.setOrderId(elements[3]);
                    }
                    break;

                case "N1":
                    // N1*BY*ABC COMPANY
                    if (elements.length > 2 && "BY".equals(elements[1])) {
                        order.setBuyerName(elements[2]);
                    }
                    break;

                case "PO1":
                    // PO1*1*10*EA*25.00**VP*ITEM100
                    if (elements.length > 7) {
                        order.setQuantity(Integer.parseInt(elements[2]));
                        order.setUnitPrice(new BigDecimal(elements[4]));
                        order.setProductCode(elements[7]);
                    }
                    break;

                default:
                    // Other EDI segments are not needed for this sample
                    break;
            }
        }

        exchange.getIn().setBody(order);
    }
}