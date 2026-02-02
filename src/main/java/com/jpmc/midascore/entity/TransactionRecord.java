package com.jpmc.midascore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private long senderId;
    @Column(nullable = false)
    private long recipientId;
    @Column(nullable = false)
    private float amount;

    protected TransactionRecord() {

    }
    public TransactionRecord(long senderId, long recipientId, float amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }
}
