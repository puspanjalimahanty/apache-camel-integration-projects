package com.example.eventdriven.service;

import org.springframework.stereotype.Service;

import com.example.eventdriven.model.FailedEvent;
import com.example.eventdriven.repository.FailedEventRepository;
import java.util.List;

@Service
public class FailedEventService {

    private final FailedEventRepository failedEventRepository;

    public FailedEventService(FailedEventRepository failedEventRepository) {
        this.failedEventRepository = failedEventRepository;
    }

    public void recordFailure(String orderId, String serviceName,
                              String errorMessage) {

        FailedEvent failedEvent = new FailedEvent(
                orderId,
                serviceName,
                errorMessage,
                "FAILED"
        );

        failedEventRepository.save(failedEvent);
    }
    public List<FailedEvent> getAllFailures() {
        return failedEventRepository.findAll();
    }
}

