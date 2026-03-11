package com.techmarket.controller;

import com.techmarket.dto.CreateDealRequest;
import com.techmarket.dto.DealResponse;
import com.techmarket.service.DealService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/deals")
public class DealController {
    private final DealService dealService;
    @Autowired
    public DealController(DealService dealService){
        this.dealService=dealService;
    }
    //Запрос на покупку
    @PostMapping("/purchase-request")
    public ResponseEntity<DealResponse> createPurchaseRequest(
            @RequestBody CreateDealRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        DealResponse response = dealService.createPurchaseRequest(request, email);
        return ResponseEntity.ok(response);
    }
    //Подтверждение сделки
    @PutMapping("/{dealId}/confirm")
    public ResponseEntity<DealResponse> confirmDeal(
            @PathVariable Long dealId,
            Authentication authentication) {
        String email = authentication.getName();
        DealResponse response = dealService.confirmDeal(dealId, email);
        return ResponseEntity.ok(response);
    }
    //Завершить сделку
    @PutMapping("/{dealId}/complete")
    public ResponseEntity<DealResponse> completeDeal(
            @PathVariable Long dealId,
            Authentication authentication) {
        String email = authentication.getName();
        DealResponse response = dealService.completeDeal(dealId, email);
        return ResponseEntity.ok(response);
    }
    //Отменить сделку
    @PutMapping("/{dealId}/cancel")
    public ResponseEntity<DealResponse> cancelDeal(
            @PathVariable Long dealId,
            Authentication authentication) {
        String email = authentication.getName();
        DealResponse response = dealService.cancelDeal(dealId, email);
        return ResponseEntity.ok(response);
    }
    //Получить сделку по id
    @GetMapping("/{dealId}")
    public ResponseEntity<DealResponse> getDealById(
            @PathVariable Long dealId,
            Authentication authentication) {
        String email = authentication.getName();
        DealResponse response = dealService.getDealById(dealId, email);
        return ResponseEntity.ok(response);
    }
    //Получить все сделки юзера
    @GetMapping("/user")
    public ResponseEntity<Page<DealResponse>> getUserDeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        String email = authentication.getName();
        Page<DealResponse> deals = dealService.getUserDeals(email, page, size, sortBy, direction);
        return ResponseEntity.ok(deals);
    }
}