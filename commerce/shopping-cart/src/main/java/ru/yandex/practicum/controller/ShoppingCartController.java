package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;
import ru.yandex.practicum.utils.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getCart(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        return shoppingCartService.getCart(userName);
    }

    @Override
    public ShoppingCartDto addProducts(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName,
            Map<UUID, @NotNull Long> products) {
        return shoppingCartService.addProducts(userName, products);
    }

    @Override
    public void clearCart(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        shoppingCartService.clearCart(userName);
    }

    @Override
    public ShoppingCartDto removeProducts(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
                                                      String userName,
                                                  List<UUID> products) {
        return shoppingCartService.removeProducts(userName, products);
    }

    @Override
    public ShoppingCartDto updateQuantity(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
                                                     String userName,
                                                 @Valid ChangeProductQuantityRequest request) {
        return shoppingCartService.updateQuantity(userName, request);
    }

    @Override
    public BookedProductsDto bookProducts(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        return shoppingCartService.bookProducts(userName);
    }
}
