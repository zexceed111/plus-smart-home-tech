package ru.yandex.practicum.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookedProductsDto {
    @PositiveOrZero(message = "Delivery weight must be positive or zero")
    Double deliveryWeight;
    
    @PositiveOrZero(message = "Delivery volume must be positive or zero")
    Double deliveryVolume;
    
    Boolean fragile;
}