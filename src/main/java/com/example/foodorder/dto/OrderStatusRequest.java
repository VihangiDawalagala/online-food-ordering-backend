package com.example.foodorder.dto;

import com.example.foodorder.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
