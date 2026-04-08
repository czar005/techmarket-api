package com.techmarket.dto;

import com.techmarket.model.Condition;

import java.math.BigDecimal;

public class ListingRequest {

    private String title;
    private String brand;
    private BigDecimal price;
    private Condition condition;

    public ListingRequest() {}

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
}