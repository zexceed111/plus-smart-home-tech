package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

public class ShoppingCartMapper {

    public static ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setShoppingCartId(shoppingCart.getId());
        shoppingCartDto.setProducts(shoppingCart.getProducts());
        return shoppingCartDto;
    }
}
