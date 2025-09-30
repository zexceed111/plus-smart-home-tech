package ru.yandex.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    private boolean fragile;
    
    @NotNull(message = "Dimensions are required")
    private DimensionDto dimension;
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    private Double weight;
}