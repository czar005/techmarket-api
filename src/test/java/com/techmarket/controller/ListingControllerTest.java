package com.techmarket.controller;

import com.techmarket.config.JwtAuthenticationFilter;
import com.techmarket.config.TestSecurityConfig;
import com.techmarket.dto.ListingFilterRequest;
import com.techmarket.dto.ListingRequest;
import com.techmarket.dto.ListingResponse;
import com.techmarket.model.Condition;
import com.techmarket.model.Status;
import com.techmarket.security.CustomUserDetailsService;
import com.techmarket.service.JwtService;
import com.techmarket.service.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
@Import(TestSecurityConfig.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListingService listingService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private ListingResponse createTestResponse(Long id, String title, String brand, BigDecimal price,
                                                Condition condition, Status status, Long ownerId) {
        ListingResponse r = new ListingResponse(id, title, brand, price, condition, status, ownerId, LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    // ==================== GET /api/listings/{id} ====================

    @Test
    @WithMockUser
    void getListingById_ShouldReturn200() throws Exception {
        ListingResponse response = createTestResponse(1L, "iPhone 14", "Apple",
                new BigDecimal("999.99"), Condition.New, Status.Active, 1L);

        when(listingService.getListingById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/listings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("iPhone 14"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.condition").value("New"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    // ==================== POST /api/listings/add ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void createListing_ShouldReturn200() throws Exception {
        ListingResponse response = createTestResponse(1L, "MacBook Pro", "Apple",
                new BigDecimal("2499.99"), Condition.New, Status.Active, 1L);

        when(listingService.createListing(any(ListingRequest.class), eq("test@example.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/listings/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"MacBook Pro\",\"brand\":\"Apple\",\"price\":2499.99,\"condition\":\"New\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("MacBook Pro"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.price").value(2499.99));
    }

    // ==================== GET /api/listings (пагинация) ====================

    @Test
    @WithMockUser
    void getAllListings_ShouldReturnPagedListings() throws Exception {
        List<ListingResponse> listings = List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, 1L),
                createTestResponse(2L, "Galaxy S24", "Samsung", new BigDecimal("899.99"), Condition.New, Status.Active, 2L)
        );
        Page<ListingResponse> page = new PageImpl<>(listings);

        when(listingService.getAllListings(0, 10, "createdAt", "desc")).thenReturn(page);

        mockMvc.perform(get("/api/listings")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("iPhone 14"))
                .andExpect(jsonPath("$.content[1].title").value("Galaxy S24"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    // ==================== POST /api/listings/filter ====================

    @Test
    @WithMockUser
    void filterListings_ShouldReturnFilteredListings() throws Exception {
        Page<ListingResponse> page = new PageImpl<>(List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, 1L)
        ));

        when(listingService.filterListings(any(ListingFilterRequest.class), eq(0), eq(10), eq("createdAt"), eq("desc")))
                .thenReturn(page);

        mockMvc.perform(post("/api/listings/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brand\":\"Apple\",\"condition\":\"New\",\"minPrice\":100,\"maxPrice\":2000}")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand").value("Apple"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ==================== GET /api/listings/user ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void getUserListings_ShouldReturnUserListings() throws Exception {
        List<ListingResponse> listings = List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, 1L),
                createTestResponse(2L, "MacBook Air", "Apple", new BigDecimal("1299.99"), Condition.New, Status.Active, 1L)
        );

        when(listingService.getUserListings("test@example.com")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("iPhone 14"))
                .andExpect(jsonPath("$[1].title").value("MacBook Air"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ==================== PUT /api/listings/{id} ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void updateListing_ShouldReturnUpdatedListing() throws Exception {
        ListingResponse updated = createTestResponse(1L, "iPhone 15", "Apple",
                new BigDecimal("1199.99"), Condition.New, Status.Active, 1L);

        when(listingService.updateListing(anyLong(), any(ListingRequest.class), eq("test@example.com")))
                .thenReturn(updated);

        mockMvc.perform(put("/api/listings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"iPhone 15\",\"brand\":\"Apple\",\"price\":1199.99,\"condition\":\"New\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("iPhone 15"))
                .andExpect(jsonPath("$.price").value(1199.99));
    }

    // ==================== DELETE /api/listings/{id} ====================

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteListing_ShouldReturn204() throws Exception {
        doNothing().when(listingService).deleteListing(1L, "test@example.com");

        mockMvc.perform(delete("/api/listings/1"))
                .andExpect(status().isNoContent());
    }

    // ==================== GET /api/listings/brand/{brand} ====================

    @Test
    @WithMockUser
    void findByBrand_ShouldReturnListings() throws Exception {
        List<ListingResponse> listings = List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, 1L),
                createTestResponse(2L, "MacBook Pro", "Apple", new BigDecimal("2499.99"), Condition.New, Status.Active, 2L)
        );

        when(listingService.findByBrand("Apple")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/brand/Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Apple"))
                .andExpect(jsonPath("$[1].brand").value("Apple"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ==================== GET /api/listings/condition/{condition} ====================

    @Test
    @WithMockUser
    void findByCondition_ShouldReturnListings() throws Exception {
        List<ListingResponse> listings = List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.Used, Status.Active, 1L)
        );

        when(listingService.findByCondition(Condition.Used)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/condition/Used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].condition").value("Used"));
    }

    // ==================== GET /api/listings/price-range ====================

    @Test
    @WithMockUser
    void findByPriceRange_ShouldReturnListings() throws Exception {
        List<ListingResponse> listings = List.of(
                createTestResponse(1L, "iPhone 14", "Apple", new BigDecimal("999.99"), Condition.New, Status.Active, 1L)
        );

        when(listingService.findByPriceRange(new BigDecimal("500"), new BigDecimal("1500")))
                .thenReturn(listings);

        mockMvc.perform(get("/api/listings/price-range")
                        .param("minPrice", "500")
                        .param("maxPrice", "1500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(999.99));
    }

    // ==================== Без аутентификации — 403 ====================

    @Test
    void createListing_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/listings/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test\",\"brand\":\"Test\",\"price\":100,\"condition\":\"New\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteListing_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/listings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateListing_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/listings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\",\"brand\":\"Test\",\"price\":100,\"condition\":\"New\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserListings_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/listings/user"))
                .andExpect(status().isForbidden());
    }
}