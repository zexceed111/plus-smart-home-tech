package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {

    UUID shoppingCartId;

    Map<UUID, Long> products;
}