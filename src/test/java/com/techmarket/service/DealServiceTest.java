package com.techmarket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techmarket.dto.CreateDealRequest;
import com.techmarket.dto.DealResponse;
import com.techmarket.model.*;
import com.techmarket.repository.DealRepository;
import com.techmarket.repository.ListingRepository;
import com.techmarket.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

  @Mock private DealRepository dealRepository;
  @Mock private ListingRepository listingRepository;
  @Mock private UserRepository userRepository;
  @InjectMocks private DealService dealService;

  @Mock private User buyer;
  @Mock private User seller;
  @Mock private Listing listing;
  private Deal deal;

  @BeforeEach
  void setUp() {
    deal = new Deal();
    deal.setId(100L);
    deal.setBuyer(buyer);
    deal.setSeller(seller);
    deal.setListing(listing);
    deal.setStatus(DealStatus.PENDING);
  }

  @Test
  void createPurchaseRequest_Success() {
    CreateDealRequest req = new CreateDealRequest();
    req.setListingId(10L);

    List<DealStatus> activeStatuses = Arrays.asList(DealStatus.PENDING, DealStatus.APPROVED);

    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(buyer.getId()).thenReturn(1L);
    when(seller.getId()).thenReturn(2L);
    when(listing.getOwner()).thenReturn(seller);
    when(listing.getStatus()).thenReturn(Status.Active);

    when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(buyer));
    when(listingRepository.findById(10L)).thenReturn(Optional.of(listing));
    when(dealRepository.existsActiveDealByListing(listing, activeStatuses)).thenReturn(false);
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);

    DealResponse result = dealService.createPurchaseRequest(req, "buyer@test.com");

    assertNotNull(result);
    assertEquals(DealStatus.PENDING, result.getStatus());
    verify(dealRepository).save(any(Deal.class));
  }

  @Test
  void createPurchaseRequest_ListingNotFound_ThrowsException() {
    CreateDealRequest req = new CreateDealRequest();
    req.setListingId(99L);

    when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(buyer));
    when(listingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class, () -> dealService.createPurchaseRequest(req, "buyer@test.com"));
  }

  @Test
  void createPurchaseRequest_BuyerIsSeller_ThrowsException() {
    CreateDealRequest req = new CreateDealRequest();
    req.setListingId(10L);

    User sameUser = mock(User.class);
    when(userRepository.findByEmail("same@test.com")).thenReturn(Optional.of(sameUser));

    assertThrows(
        RuntimeException.class, () -> dealService.createPurchaseRequest(req, "same@test.com"));
  }

  @Test
  void confirmDeal_Success() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(seller.getEmail()).thenReturn("seller@test.com");
    when(listingRepository.save(any(Listing.class))).thenReturn(listing);
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);

    DealResponse result = dealService.confirmDeal(100L, "seller@test.com");

    assertNotNull(result);
    assertEquals(DealStatus.APPROVED, result.getStatus());
  }

  @Test
  void confirmDeal_NotFound_ThrowsException() {
    when(dealRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> dealService.confirmDeal(99L, "seller@test.com"));
  }

  @Test
  void confirmDeal_NotSeller_ThrowsException() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(seller.getEmail()).thenReturn("seller@test.com");

    assertThrows(RuntimeException.class, () -> dealService.confirmDeal(100L, "wrong@test.com"));
  }

  @Test
  void getDealById_Found_AsBuyer() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    DealResponse result = dealService.getDealById(100L, "buyer@test.com");

    assertNotNull(result);
    assertEquals(100L, result.getId());
  }

  @Test
  void getDealById_Found_AsSeller() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    DealResponse result = dealService.getDealById(100L, "seller@test.com");

    assertNotNull(result);
    assertEquals(100L, result.getId());
  }

  @Test
  void getDealById_NotFound_ThrowsException() {
    when(dealRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> dealService.getDealById(99L, "buyer@test.com"));
  }

  @Test
  void getDealById_NotParticipant_ThrowsException() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    assertThrows(RuntimeException.class, () -> dealService.getDealById(100L, "stranger@test.com"));
  }

  @Test
  void cancelDeal_Success_ByBuyer() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    DealResponse result = dealService.cancelDeal(100L, "buyer@test.com");

    assertNotNull(result);
    assertEquals(DealStatus.CANCELLED, result.getStatus());
  }

  @Test
  void cancelDeal_Success_BySeller() {
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    DealResponse result = dealService.cancelDeal(100L, "seller@test.com");

    assertNotNull(result);
    assertEquals(DealStatus.CANCELLED, result.getStatus());
  }

  @Test
  void completeDeal_Success() {
    deal.setStatus(DealStatus.APPROVED);
    when(dealRepository.findById(100L)).thenReturn(Optional.of(deal));
    when(listingRepository.save(any(Listing.class))).thenReturn(listing);
    when(dealRepository.save(any(Deal.class))).thenReturn(deal);
    when(buyer.getEmail()).thenReturn("buyer@test.com");
    when(seller.getEmail()).thenReturn("seller@test.com");

    DealResponse result = dealService.completeDeal(100L, "buyer@test.com");

    assertNotNull(result);
    assertEquals(DealStatus.COMPLETED, result.getStatus());
  }
}
