package ru.yandex.practicum.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {
    private UUID productId;
    private boolean fragile;
    private DimensionDto dimension;
    private double weight;
}
