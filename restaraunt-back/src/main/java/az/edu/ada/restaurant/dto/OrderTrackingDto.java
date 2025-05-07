package az.edu.ada.restaurant.dto;

import java.time.Instant;
import java.util.List;

public record OrderTrackingDto(
        String trackCode,
        String status,
        Instant createdAt,
        Instant estimatedReadyAt,
        List<OrderItemDto> items
) {}