package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {

    Double deliveryWeight;

    Double deliveryVolume;

    boolean fragile;
}
