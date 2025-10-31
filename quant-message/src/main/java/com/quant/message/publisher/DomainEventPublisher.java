package com.quant.message.publisher;

import com.quant.common.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Domain Event Publisher
 */
@Slf4j
@Component
public class DomainEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String exchange, String routingKey, DomainEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Published domain event: {} to exchange: {}, routingKey: {}",
                    event.getEventType(), exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish domain event: {}", event.getEventType(), e);
            throw e;
        }
    }
}
