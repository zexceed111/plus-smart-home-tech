package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddNewProductInWarehouseRequest {

    @NotNull
    UUID productId;

    boolean fragile;

    @NotNull
    DimensionDto dimension;

    @NotNull
    @DecimalMin(value = "1", message = "Вес товара должен быть >= 1")
    Double weight;
}
