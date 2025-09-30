package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getCart(String userName);

    ShoppingCartDto addProducts(String userName, Map<UUID, Long> products);

    void clearCart(String userName);

    ShoppingCartDto removeProducts(String userName, List<UUID> products);

    ShoppingCartDto updateQuantity(String userName, ChangeProductQuantityRequest request);

    BookedProductsDto bookProducts(String userName);
}
