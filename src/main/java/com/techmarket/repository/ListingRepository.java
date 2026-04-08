package com.techmarket.repository;

import com.techmarket.model.Listing;
import com.techmarket.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByOwner(com.techmarket.model.User owner);
    
    List<Listing> findByStatus(Status status);
    
    List<Listing> findByBrand(String brand);
    
    List<Listing> findByCondition(com.techmarket.model.Condition condition);
    
    List<Listing> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Listing> findByBrandAndCondition(String brand, com.techmarket.model.Condition condition);
    
    List<Listing> findByBrandAndPriceBetween(String brand, BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Listing> findByConditionAndPriceBetween(com.techmarket.model.Condition condition, BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Listing> findByBrandAndConditionAndPriceBetween(
        String brand, 
        com.techmarket.model.Condition condition, 
        BigDecimal minPrice, 
        BigDecimal maxPrice
    );
    
    Page<Listing> findByStatus(Status status, Pageable pageable);
    
    @Query("SELECT l FROM Listing l WHERE " +
           "(:brand IS NULL OR l.brand = :brand) AND " +
           "(:condition IS NULL OR l.condition = :condition) AND " +
           "(:minPrice IS NULL OR l.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR l.price <= :maxPrice) AND " +
           "l.status = :status")
    Page<Listing> findWithFilters(
        @Param("brand") String brand,
        @Param("condition") com.techmarket.model.Condition condition,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("status") Status status,
        Pageable pageable
    );
}