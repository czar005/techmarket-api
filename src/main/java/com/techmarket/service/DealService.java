package com.techmarket.service;

import com.techmarket.dto.CreateDealRequest;
import com.techmarket.dto.DealResponse;
import com.techmarket.dto.UpdateDealStatusRequest;
import com.techmarket.model.*;
import com.techmarket.repository.DealRepository;
import com.techmarket.repository.ListingRepository;
import com.techmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class DealService {
    private final DealRepository dealRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    @Autowired
    public DealService(DealRepository dealRepository,ListingRepository listingRepository,UserRepository userRepository) {
        this.dealRepository=dealRepository;
        this.userRepository=userRepository;
        this.listingRepository=listingRepository;
    }
    //Запрос на покупку.
    @Transactional
    public DealResponse createPurchaseRequest(CreateDealRequest request, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail).orElseThrow(() -> new RuntimeException("Покупатель не найден"));
        Listing listing = listingRepository.findById(request.getListingId()).orElseThrow(() -> new RuntimeException("Объявление не найдено"));
        if (listing.getStatus() != Status.Active) {throw new RuntimeException("Объявление недоступно");}
        if (listing.getOwner().getId().equals(buyer.getId())) {throw new RuntimeException("Нельзя откликнуться на своё же объявление");}

        List<DealStatus> activeStatuses = Arrays.asList(DealStatus.PENDING, DealStatus.APPROVED);

        if (dealRepository.existsActiveDealByListing(listing, activeStatuses)) {throw new RuntimeException("По этому объявлению уже есть активная сделка");}

        Deal deal = new Deal(listing, buyer, listing.getOwner());
        Deal savedDeal = dealRepository.save(deal);

        return convertToDealResponse(savedDeal);
    }

    //Подтверждение продавцом
    @Transactional
    public DealResponse confirmDeal(Long dealId, String sellerEmail) {
        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new RuntimeException("Сделка не найдена"));

        if (!deal.getSeller().getEmail().equals(sellerEmail)) {throw new RuntimeException("Только продавец может подтвердить сделку");}
        if (deal.getStatus() != DealStatus.PENDING) {throw new RuntimeException("Сделку можно подтвердить только в статусе PENDING");}

        deal.setStatus(DealStatus.APPROVED);

        Listing listing = deal.getListing();
        listing.setStatus(Status.Reserved);
        listingRepository.save(listing);

        Deal updatedDeal = dealRepository.save(deal);
        return convertToDealResponse(updatedDeal);
    }

    //Завершение
    @Transactional
    public DealResponse completeDeal(Long dealId, String userEmail) {
        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new RuntimeException("Сделка не найдена"));

        if (!deal.getSeller().getEmail().equals(userEmail) && !deal.getBuyer().getEmail().equals(userEmail)) {throw new RuntimeException("Только участники могут завершить сделку");}

        if (deal.getStatus() != DealStatus.APPROVED) {throw new RuntimeException("Сделку можно завершить только в статусе APPROVED");}

        deal.setStatus(DealStatus.COMPLETED);

        Listing listing = deal.getListing();
        listing.setStatus(Status.Sold);
        listingRepository.save(listing);

        Deal updatedDeal = dealRepository.save(deal);
        return convertToDealResponse(updatedDeal);
    }
    //Отмена
    @Transactional
    public DealResponse cancelDeal(Long dealId, String userEmail) {
        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new RuntimeException("Сделка не найдена"));

        if (!deal.getSeller().getEmail().equals(userEmail) && !deal.getBuyer().getEmail().equals(userEmail)) {throw new RuntimeException("Только участники могут отменить сделку");}

        if (deal.getStatus() == DealStatus.COMPLETED) {throw new RuntimeException("Нельзя отменить завершенную сделку");}

        if (deal.getStatus() == DealStatus.APPROVED) {Listing listing = deal.getListing();listing.setStatus(Status.Active);listingRepository.save(listing);}

        deal.setStatus(DealStatus.CANCELLED);
        Deal updatedDeal = dealRepository.save(deal);
        return convertToDealResponse(updatedDeal);
    }
    //Получение всех сделок по ID
    public DealResponse getDealById(Long id, String userEmail) {
        Deal deal = dealRepository.findById(id).orElseThrow(() -> new RuntimeException("Сделка не найдена"));

        if (!deal.getSeller().getEmail().equals(userEmail) && !deal.getBuyer().getEmail().equals(userEmail)) {throw new RuntimeException("Вы не участник этой сделки");}

        return convertToDealResponse(deal);
    }
    //Все сделки юзера
    public Page<DealResponse> getUserDeals(String userEmail, int page, int size, String sortBy, String direction) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return dealRepository.findUserDeals(user, pageable).map(this::convertToDealResponse);
    }
    //Конвертер с Deal в DealResponse. для безопасности. без этого пользователь получит то что не должен видеть. чтото вроде этого
    // {
    //    "id": 1,
    //    "buyer": {
    //        "id": 2,
    //        "email": "user@mail.com",
    //        "password": "123456"
    //    }
    //}
    //а должен только id и почту
    private DealResponse convertToDealResponse(Deal deal) {
        DealResponse response = new DealResponse(
                deal.getId(),
                deal.getListing().getId(),
                deal.getListing().getTitle(),
                deal.getBuyer().getId(),
                deal.getBuyer().getEmail(),
                deal.getSeller().getId(),
                deal.getSeller().getEmail(),
                deal.getStatus(),
                deal.getCreatedAt()
        );
        response.setUpdatedAt(deal.getUpdatedAt());
        response.setCompletedAt(deal.getCompletedAt());
        return response;
    }

}
