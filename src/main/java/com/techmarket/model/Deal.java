package com.techmarket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) //я вот думаю, а надо ли оно вообще. много сделок к одному объявлению
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    @Enumerated(EnumType.STRING)
    private DealStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Deal () {}
    public Deal(Listing listing, User buyer, User seller){
        this.listing = listing;
        this.status = DealStatus.PENDING;
        this.buyer = buyer;
        this.seller = seller;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public Listing getListing() {return listing;}
    public User getBuyer() {return buyer;}
    public User getSeller() {return seller;}
    public DealStatus getStatus() {return status;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public LocalDateTime getCompletedAt() {return completedAt;}

    public void setId(Long id) {this.id = id;}
    public void setListing(Listing listing) {
        this.listing = listing;
        this.updatedAt = LocalDateTime.now();
    }
    public void setBuyer(User buyer) {
        this.buyer = buyer;
        this.updatedAt = LocalDateTime.now();
    }
    public void setSeller(User seller) {
        this.seller = seller;
        this.updatedAt = LocalDateTime.now();
    }
    public void setStatus(DealStatus status){
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if(status == DealStatus.COMPLETED){this.completedAt = LocalDateTime.now();}
    }

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}
    public void setCompletedAt(LocalDateTime completedAt) {this.completedAt = completedAt;}
}