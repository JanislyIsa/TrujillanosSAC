package com.example.TrujillanosSAC.Dto;

import java.util.List;

public record CheckoutRequest(
        String metodoPago,
        List<CheckoutItemRequest> items
) {
}
