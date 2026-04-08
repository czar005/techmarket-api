package com.techmarket.service;

import com.techmarket.dto.ListingRequest;
import com.techmarket.dto.ListingResponse;
import com.techmarket.dto.ListingFilterRequest;
import com.techmarket.model.Condition;
import com.techmarket.model.Listing;
import com.techmarket.model.Status;
import com.techmarket.model.User;
import com.techmarket.repository.ListingRepository;
import com.techmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    public ListingResponse createListing(ListingRequest request, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Listing listing = new Listing(
                request.getTitle(),
                request.getBrand(),
                request.getPrice(),
                request.getCondition(),
                Status.Active,
                owner
        );

        Listing savedListing = listingRepository.save(listing);
        
        return convertToListingResponse(savedListing);
    }

    public ListingResponse getListingById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        return convertToListingResponse(listing);
    }

    public List<ListingResponse> getUserListings(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Listing> listings = listingRepository.findByOwner(owner);
        
        return listings.stream()
                .map(this::convertToListingResponse)
                .collect(Collectors.toList());
    }

    public ListingResponse updateListing(Long id, ListingRequest request, String email) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Проверка, что пользователь является владельцем
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!listing.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own listings");
        }

        listing.setTitle(request.getTitle());
        listing.setBrand(request.getBrand());
        listing.setPrice(request.getPrice());
        listing.setCondition(request.getCondition());

        Listing updatedListing = listingRepository.save(listing);
        
        return convertToListingResponse(updatedListing);
    }

    public void deleteListing(Long id, String email) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Проверка, что пользователь является владельцем
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!listing.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own listings");
        }

        listingRepository.delete(listing);
    }

    public Page<ListingResponse> getAllListings(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return listingRepository.findByStatus(Status.Active, pageable)
                .map(this::convertToListingResponse);
    }

    public Page<ListingResponse> filterListings(ListingFilterRequest filter, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return listingRepository.findWithFilters(
                filter.getBrand(),
                filter.getCondition(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                Status.Active,
                pageable
        ).map(this::convertToListingResponse);
    }

    public List<ListingResponse> findByBrand(String brand) {
        List<Listing> listings = listingRepository.findByBrand(brand);
        
        return listings.stream()
                .map(this::convertToListingResponse)
                .collect(Collectors.toList());
    }

    public List<ListingResponse> findByCondition(Condition condition) {
        List<Listing> listings = listingRepository.findByCondition(condition);
        
        return listings.stream()
                .map(this::convertToListingResponse)
                .collect(Collectors.toList());
    }

    public List<ListingResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Listing> listings = listingRepository.findByPriceBetween(minPrice, maxPrice);
        
        return listings.stream()
                .map(this::convertToListingResponse)
                .collect(Collectors.toList());
    }

    private ListingResponse convertToListingResponse(Listing listing) {
        ListingResponse response = new ListingResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getBrand(),
                listing.getPrice(),
                listing.getCondition(),
                listing.getStatus(),
                listing.getOwner().getId(),
                listing.getCreatedAt()
        );
        response.setUpdatedAt(listing.getUpdatedAt());
        return response;
    }
}