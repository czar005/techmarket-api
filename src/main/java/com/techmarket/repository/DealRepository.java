package com.techmarket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techmarket.model.Deal;
import com.techmarket.model.DealStatus;
import com.techmarket.model.User;
import com.techmarket.model.Listing;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Slice;
@Repository
public interface DealRepository extends JpaRepository<Deal,Long>{
    List<Deal> findByBuyer(User buyer);
    List<Deal> findBySeller(User seller);
    @Query("SELECT d FROM Deal d WHERE d.listing = :listing AND d.status IN :statuses") //ищу активную сделку
    Optional<Deal> findActiveDealByListing(@Param("listing")Listing listing, @Param("statuses")List<DealStatus> statuses);

    @Query("SELECT COUNT(d) > 0 FROM Deal d WHERE d.listing = :listing AND d.status IN :statuses") //проверка на наличие активной сделки
    boolean existsActiveDealByListing(@Param("listing")Listing listing, @Param("statuses")List<DealStatus> statuses);

    /*@Query("SELECT d FROM Deal d WHERE d.buyer = :user OR d.seller = :user")
    Page<Deal> findUserDealsPage(@Param("user") User user, Pageable pageable); //TODO num1: возможно стоит использовать Slice вместо Page. Если так то придется код кое-где изменить.

    @Query("SELECT d FROM Deal d WHERE d.buyer = :user OR d.seller = :user")
    Slice<Deal> findUserDealsSlice(@Param("user") User user, Pageable pageable);
    // короче я просто решил сделать так*/
    //TODO num2: пока пусть будет так.
    @Query("SELECT d FROM Deal d WHERE d.buyer = :user OR d.seller = :user")
    Page<Deal> findUserDeals(@Param("user") User user, Pageable pageable);
}
