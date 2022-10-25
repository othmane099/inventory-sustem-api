package com.ombdev.inventorysystemapi.response.product;

import com.ombdev.inventorysystemapi.response.category.CategoryResponse;

import java.time.LocalDateTime;
import java.util.Set;

public record ProductResponse(Long id, String productCode, String productName, Integer quantity, Double buyingPrice,
                              Double sellingPrice, LocalDateTime created_at, Set<CategoryResponse> categories) {
}
