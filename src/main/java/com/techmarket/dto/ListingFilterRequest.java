package com.techmarket.dto;

import com.techmarket.model.Condition;

import java.math.BigDecimal;

public class ListingFilterRequest {

    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Condition condition;

    public ListingFilterRequest() {}

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}