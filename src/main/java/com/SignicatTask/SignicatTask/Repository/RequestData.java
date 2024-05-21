package com.SignicatTask.SignicatTask.Repository;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Represents a log of a request. Uses combination index to
 * allow fast cross reference of date and IP address.
 */
@Entity
@Table(name = "request_data", indexes = {
        @Index(name = "idx_ip_date", columnList = "ipAddress, date")
})
public class RequestData {
    public static enum Status {
        FAIL,
        SUCCESS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public LocalDate date;

    @Column(nullable = false)
    public String ipAddress;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Status status;

    public RequestData() {
    }

    public RequestData(LocalDate date, String ipAddress, Status status) {
        this.date = date;
        this.ipAddress = ipAddress;
        this.status = status;

    }
}
