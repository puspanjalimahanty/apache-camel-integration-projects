package com.example.edifileprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edifileprocessing.entity.PurchaseOrderEntity;
import com.example.edifileprocessing.exception.DuplicatePurchaseOrderException;
import com.example.edifileprocessing.model.PurchaseOrder;
import com.example.edifileprocessing.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repository;

    public PurchaseOrderService(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(PurchaseOrder order) {
    	
    	if (repository.existsById(order.getOrderId())) {
            throw new DuplicatePurchaseOrderException(
                    "Duplicate purchase order: " + order.getOrderId());
        }

        PurchaseOrderEntity entity = new PurchaseOrderEntity();

        entity.setOrderId(order.getOrderId());
        entity.setBuyerName(order.getBuyerName());
        entity.setProductCode(order.getProductCode());
        entity.setQuantity(order.getQuantity());
        entity.setUnitPrice(order.getUnitPrice());

        repository.save(entity);
    }
}