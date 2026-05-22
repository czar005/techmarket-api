package com.techmarket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.techmarket.dto.ListingFilterRequest;
import com.techmarket.dto.ListingRequest;
import com.techmarket.dto.ListingResponse;
import com.techmarket.model.Condition;
import com.techmarket.model.Listing;
import com.techmarket.model.Status;
import com.techmarket.model.User;
import com.techmarket.repository.ListingRepository;
import com.techmarket.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

  @Mock private ListingRepository listingRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private ListingService listingService;

  private User testUser;
  private User otherUser;
  private Listing testListing;
  private ListingRequest testRequest;
  private ListingFilterRequest testFilter;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "password", "USER");
    ReflectionTestUtils.setField(testUser, "id", 1L);

    otherUser = new User("other@example.com", "password", "USER");
    ReflectionTestUtils.setField(otherUser, "id", 2L);

    testListing =
        new Listing(
            "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, testUser);
    ReflectionTestUtils.setField(testListing, "id", 1L);
    ReflectionTestUtils.setField(testListing, "createdAt", LocalDateTime.now());
    ReflectionTestUtils.setField(testListing, "updatedAt", LocalDateTime.now());

    testRequest = new ListingRequest();
    testRequest.setTitle("iPhone 14");
    testRequest.setBrand("Apple");
    testRequest.setPrice(new BigDecimal("999.99"));
    testRequest.setCondition(Condition.New);

    testFilter = new ListingFilterRequest();
    testFilter.setBrand("Apple");
    testFilter.setCondition(Condition.New);
    testFilter.setMinPrice(new BigDecimal("100"));
    testFilter.setMaxPrice(new BigDecimal("2000"));
  }

  // ==================== createListing ====================

  @Test
  void createListing_Success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(listingRepository.save(any(Listing.class))).thenReturn(testListing);

    ListingResponse response = listingService.createListing(testRequest, "test@example.com");

    assertNotNull(response);
    assertEquals("iPhone 14", response.getTitle());
    assertEquals("Apple", response.getBrand());
    assertEquals(new BigDecimal("999.99"), response.getPrice());
    assertEquals(Condition.New, response.getCondition());
    assertEquals(Status.Active, response.getStatus());
    assertEquals(1L, response.getOwnerId());

    verify(userRepository).findByEmail("test@example.com");
    verify(listingRepository).save(any(Listing.class));
  }

  @Test
  void createListing_UserNotFound_ThrowsException() {
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingService.createListing(testRequest, "unknown@example.com"));

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findByEmail("unknown@example.com");
    verify(listingRepository, never()).save(any());
  }

  // ==================== getListingById ====================

  @Test
  void getListingById_Success() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

    ListingResponse response = listingService.getListingById(1L);

    assertNotNull(response);
    assertEquals("iPhone 14", response.getTitle());
    assertEquals(1L, response.getId());

    verify(listingRepository).findById(1L);
  }

  @Test
  void getListingById_NotFound_ThrowsException() {
    when(listingRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> listingService.getListingById(999L));

    assertEquals("Listing not found", exception.getMessage());
    verify(listingRepository).findById(999L);
  }

  // ==================== getUserListings ====================

  @Test
  void getUserListings_Success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(listingRepository.findByOwner(testUser)).thenReturn(List.of(testListing));

    List<ListingResponse> responses = listingService.getUserListings("test@example.com");

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals("iPhone 14", responses.get(0).getTitle());

    verify(userRepository).findByEmail("test@example.com");
    verify(listingRepository).findByOwner(testUser);
  }

  @Test
  void getUserListings_UserNotFound_ThrowsException() {
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> listingService.getUserListings("unknown@example.com"));

    assertEquals("User not found", exception.getMessage());
    verify(listingRepository, never()).findByOwner(any());
  }

  // ==================== updateListing ====================

  @Test
  void updateListing_ByOwner_Success() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(listingRepository.save(any(Listing.class))).thenReturn(testListing);

    ListingRequest updateRequest = new ListingRequest();
    updateRequest.setTitle("iPhone 15");
    updateRequest.setBrand("Apple");
    updateRequest.setPrice(new BigDecimal("1199.99"));
    updateRequest.setCondition(Condition.New);

    ListingResponse response = listingService.updateListing(1L, updateRequest, "test@example.com");

    assertNotNull(response);

    verify(listingRepository).findById(1L);
    verify(userRepository).findByEmail("test@example.com");
    verify(listingRepository).save(any(Listing.class));
  }

  @Test
  void updateListing_NotOwner_ThrowsException() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
    when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingService.updateListing(1L, testRequest, "other@example.com"));

    assertEquals("You can only update your own listings", exception.getMessage());
    verify(listingRepository).findById(1L);
    verify(userRepository).findByEmail("other@example.com");
    verify(listingRepository, never()).save(any());
  }

  @Test
  void updateListing_ListingNotFound_ThrowsException() {
    when(listingRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingService.updateListing(999L, testRequest, "test@example.com"));

    assertEquals("Listing not found", exception.getMessage());
    verify(listingRepository).findById(999L);
    verify(listingRepository, never()).save(any());
  }

  @Test
  void updateListing_UserNotFound_ThrowsException() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> listingService.updateListing(1L, testRequest, "unknown@example.com"));

    assertEquals("User not found", exception.getMessage());
    verify(listingRepository, never()).save(any());
  }

  // ==================== deleteListing ====================

  @Test
  void deleteListing_ByOwner_Success() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    listingService.deleteListing(1L, "test@example.com");

    verify(listingRepository).findById(1L);
    verify(userRepository).findByEmail("test@example.com");
    verify(listingRepository).delete(testListing);
  }

  @Test
  void deleteListing_NotOwner_ThrowsException() {
    when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
    when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> listingService.deleteListing(1L, "other@example.com"));

    assertEquals("You can only delete your own listings", exception.getMessage());
    verify(listingRepository).findById(1L);
    verify(userRepository).findByEmail("other@example.com");
    verify(listingRepository, never()).delete(any());
  }

  @Test
  void deleteListing_ListingNotFound_ThrowsException() {
    when(listingRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> listingService.deleteListing(999L, "test@example.com"));

    assertEquals("Listing not found", exception.getMessage());
    verify(listingRepository).findById(999L);
    verify(listingRepository, never()).delete(any());
  }

  // ==================== getAllListings ====================

  @Test
  void getAllListings_Success() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Listing> listingPage = new PageImpl<>(List.of(testListing));

    when(listingRepository.findByStatus(Status.Active, pageable)).thenReturn(listingPage);

    Page<ListingResponse> result = listingService.getAllListings(0, 10, "createdAt", "desc");

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals("iPhone 14", result.getContent().get(0).getTitle());

    verify(listingRepository).findByStatus(eq(Status.Active), any(Pageable.class));
  }

  @Test
  void getAllListings_SortedAsc_Success() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("price").ascending());
    Page<Listing> listingPage = new PageImpl<>(List.of(testListing));

    when(listingRepository.findByStatus(Status.Active, pageable)).thenReturn(listingPage);

    Page<ListingResponse> result = listingService.getAllListings(0, 10, "price", "asc");

    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    verify(listingRepository).findByStatus(eq(Status.Active), any(Pageable.class));
  }

  // ==================== filterListings ====================

  @Test
  void filterListings_Success() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Listing> listingPage = new PageImpl<>(List.of(testListing));

    when(listingRepository.findWithFilters(
            eq("Apple"), eq(Condition.New),
            eq(new BigDecimal("100")), eq(new BigDecimal("2000")),
            eq(Status.Active), any(Pageable.class)))
        .thenReturn(listingPage);

    Page<ListingResponse> result =
        listingService.filterListings(testFilter, 0, 10, "createdAt", "desc");

    assertNotNull(result);
    assertEquals(1, result.getContent().size());

    verify(listingRepository)
        .findWithFilters(
            eq("Apple"), eq(Condition.New),
            eq(new BigDecimal("100")), eq(new BigDecimal("2000")),
            eq(Status.Active), any(Pageable.class));
  }

  // ==================== findByBrand ====================

  @Test
  void findByBrand_Success() {
    when(listingRepository.findByBrand("Apple")).thenReturn(List.of(testListing));

    List<ListingResponse> responses = listingService.findByBrand("Apple");

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals("Apple", responses.get(0).getBrand());

    verify(listingRepository).findByBrand("Apple");
  }

  @Test
  void findByBrand_EmptyResult() {
    when(listingRepository.findByBrand("Samsung")).thenReturn(List.of());

    List<ListingResponse> responses = listingService.findByBrand("Samsung");

    assertNotNull(responses);
    assertTrue(responses.isEmpty());

    verify(listingRepository).findByBrand("Samsung");
  }

  // ==================== findByCondition ====================

  @Test
  void findByCondition_Success() {
    when(listingRepository.findByCondition(Condition.Used)).thenReturn(List.of(testListing));

    List<ListingResponse> responses = listingService.findByCondition(Condition.Used);

    assertNotNull(responses);
    assertEquals(1, responses.size());

    verify(listingRepository).findByCondition(Condition.Used);
  }

  // ==================== findByPriceRange ====================

  @Test
  void findByPriceRange_Success() {
    BigDecimal min = new BigDecimal("500");
    BigDecimal max = new BigDecimal("1500");
    when(listingRepository.findByPriceBetween(min, max)).thenReturn(List.of(testListing));

    List<ListingResponse> responses = listingService.findByPriceRange(min, max);

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(new BigDecimal("999.99"), responses.get(0).getPrice());

    verify(listingRepository).findByPriceBetween(min, max);
  }

  @Test
  void findByPriceRange_EmptyResult() {
    BigDecimal min = new BigDecimal("1");
    BigDecimal max = new BigDecimal("10");
    when(listingRepository.findByPriceBetween(min, max)).thenReturn(List.of());

    List<ListingResponse> responses = listingService.findByPriceRange(min, max);

    assertNotNull(responses);
    assertTrue(responses.isEmpty());

    verify(listingRepository).findByPriceBetween(min, max);
  }
}
