package az.edu.ada.restaurant.dto;

public record OrderItemDto(
        Long   id,
        String dishName,
        int    quantity,
        String comment,
        double price
) {}