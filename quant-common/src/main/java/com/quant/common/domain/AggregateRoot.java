package com.quant.common.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for DDD
 */
public abstract class AggregateRoot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private transient final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Register domain event
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * Get and clear domain events
     */
    public List<DomainEvent> getDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    /**
     * Clear domain events
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
