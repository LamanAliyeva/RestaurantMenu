package az.edu.ada.restaurant.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING,
    READY,
    SERVED,
    COMPLETED;

    @JsonValue
    public String toJson() { return name().toLowerCase(); }
}