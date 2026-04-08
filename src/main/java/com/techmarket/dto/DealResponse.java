package com.techmarket.dto;

import com.techmarket.model.DealStatus;

import java.time.LocalDateTime;

public class DealResponse {
    private Long id;
    private Long listingId;
    private String listingTitle;
    private Long buyerId;
    private String buyerEmail;
    private DealStatus status;
    private Long sellerId;
    private String sellerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    public DealResponse(){}
    public DealResponse(Long id,Long listingId,String listingTitle,Long buyerId,String buyerEmail,Long sellerId,String sellerEmail,DealStatus status,LocalDateTime createdAt){
        this.id=id;
        this.listingId=listingId;
        this.listingTitle=listingTitle;
        this.buyerId=buyerId;
        this.buyerEmail=buyerEmail;
        this.sellerId=sellerId;
        this.sellerEmail=sellerEmail;
        this.status=status;
        this.createdAt=createdAt;
    }
    public Long getId() {return id;}
    public Long getListingId() {return listingId;}
    public String getListingTitle() {return listingTitle;}
    public Long getBuyerId() {return buyerId;}
    public String getBuyerEmail() {return buyerEmail;}
    public DealStatus getStatus() {return status;}
    public Long getSellerId() {return sellerId;}
    public String getSellerEmail() {return sellerEmail;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public LocalDateTime getCompletedAt() {return completedAt;}

    public void setId(Long id) {this.id = id;}
    public void setListingId(Long listingId) {this.listingId = listingId;}
    public void setListingTitle(String listingTitle) {this.listingTitle = listingTitle;}
    public void setBuyerId(Long buyerId) {this.buyerId = buyerId;}
    public void setBuyerEmail(String buyerEmail) {this.buyerEmail = buyerEmail;}
    public void setStatus(DealStatus status) {this.status = status;}
    public void setSellerId(Long sellerId) {this.sellerId = sellerId;}
    public void setSellerEmail(String sellerEmail) {this.sellerEmail = sellerEmail;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}
    public void setCompletedAt(LocalDateTime completedAt) {this.completedAt = completedAt;}
}