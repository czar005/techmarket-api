package com.techmarket.dto;

import com.techmarket.model.Condition;
import com.techmarket.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingResponse {

    private Long id;
    private String title;
    private String brand;
    private BigDecimal price;
    private Condition condition;
    private Status status;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ListingResponse() {}

    public ListingResponse(Long id, String title, String brand, BigDecimal price, 
                          Condition condition, Status status, Long ownerId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.price = price;
        this.condition = condition;
        this.status = status;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}