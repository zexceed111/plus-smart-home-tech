package ru.yandex.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {
    @NotNull(message = "Product ID must not be null")
    private UUID productId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be positive")
    private Long quantity;
}