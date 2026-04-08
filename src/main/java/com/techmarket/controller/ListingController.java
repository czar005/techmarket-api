package com.techmarket.controller;

import com.techmarket.dto.ListingRequest;
import com.techmarket.dto.ListingResponse;
import com.techmarket.dto.ListingFilterRequest;
import com.techmarket.model.Condition;
import com.techmarket.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    @Autowired
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    // Создание объявления
    @PostMapping("/add")
    public ResponseEntity<ListingResponse> createListing(@RequestBody ListingRequest request, Authentication authentication) {
        String email = authentication.getName();
        ListingResponse response = listingService.createListing(request, email);
        return ResponseEntity.ok(response);
    }

    // Получение объявления по ID
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListing(@PathVariable Long id) {
        ListingResponse response = listingService.getListingById(id);
        return ResponseEntity.ok(response);
    }

    // Получение всех объявлений с пагинацией и сортировкой
    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getAllListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Page<ListingResponse> listings = listingService.getAllListings(page, size, sortBy, direction);
        return ResponseEntity.ok(listings);
    }

    // Фильтрация объявлений
    @PostMapping("/filter")
    public ResponseEntity<Page<ListingResponse>> filterListings(
            @RequestBody ListingFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Page<ListingResponse> listings = listingService.filterListings(filter, page, size, sortBy, direction);
        return ResponseEntity.ok(listings);
    }

    // Получение объявлений пользователя
    @GetMapping("/user")
    public ResponseEntity<List<ListingResponse>> getUserListings(Authentication authentication) {
        String email = authentication.getName();
        List<ListingResponse> listings = listingService.getUserListings(email);
        return ResponseEntity.ok(listings);
    }

    // Обновление объявления
    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody ListingRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        ListingResponse response = listingService.updateListing(id, request, email);
        return ResponseEntity.ok(response);
    }

    // Удаление объявления
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        listingService.deleteListing(id, email);
        return ResponseEntity.noContent().build();
    }

    // Фильтрация по бренду
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ListingResponse>> getByBrand(@PathVariable String brand) {
        List<ListingResponse> listings = listingService.findByBrand(brand);
        return ResponseEntity.ok(listings);
    }

    // Фильтрация по состоянию
    @GetMapping("/condition/{condition}")
    public ResponseEntity<List<ListingResponse>> getByCondition(@PathVariable Condition condition) {
        List<ListingResponse> listings = listingService.findByCondition(condition);
        return ResponseEntity.ok(listings);
    }

    // Фильтрация по диапазону цен
    @GetMapping("/price-range")
    public ResponseEntity<List<ListingResponse>> getByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        
        List<ListingResponse> listings = listingService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(listings);
    }
}