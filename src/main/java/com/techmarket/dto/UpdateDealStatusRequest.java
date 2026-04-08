package com.techmarket.dto;

import com.techmarket.model.DealStatus;

public class UpdateDealStatusRequest {
    private DealStatus status;
    public UpdateDealStatusRequest(){}
    public DealStatus getStatus() {return status;}
    public void setStatus(DealStatus status) {this.status = status;}
}