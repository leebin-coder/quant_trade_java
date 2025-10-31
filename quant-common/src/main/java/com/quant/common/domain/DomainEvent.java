package com.quant.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base Domain Event for DDD
 */
@Data
public abstract class DomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Event ID
     */
    private String eventId;

    /**
     * Event timestamp
     */
    private LocalDateTime occurredOn;

    /**
     * Event type
     */
    private String eventType;

    public DomainEvent() {
        this.occurredOn = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }
}
