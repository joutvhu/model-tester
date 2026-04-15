package com.joutvhu.model.tester.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.temporal.Temporal;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

public class ReflectionModel {
    private Set<String> categories;
    private String identifier;
    private Queue<String> history;
    private Temporal eventTime;
    private SortedSet<Integer> indexes;
    private AbstractMapModel details;

    public ReflectionModel(Set<String> categories, String identifier, Queue<String> history, Temporal eventTime, SortedSet<Integer> indexes) {
        this.categories = categories;
        this.identifier = identifier;
        this.history = history;
        this.eventTime = eventTime;
        this.indexes = indexes;
    }

    public AbstractMapModel getDetails() {
        return details;
    }

    public void setDetails(AbstractMapModel details) {
        this.details = details;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Queue<String> getHistory() {
        return history;
    }

    public void setHistory(Queue<String> history) {
        this.history = history;
    }

    public Temporal getEventTime() {
        return eventTime;
    }

    public void setEventTime(Temporal eventTime) {
        this.eventTime = eventTime;
    }

    public SortedSet<Integer> getIndexes() {
        return indexes;
    }

    public void setIndexes(SortedSet<Integer> indexes) {
        this.indexes = indexes;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
