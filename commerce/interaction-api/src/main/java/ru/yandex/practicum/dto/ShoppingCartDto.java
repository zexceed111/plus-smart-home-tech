package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {
    UUID id;
    
    @NotEmpty(message = "Products map cannot be empty")
    Map<UUID, Long> products;
}