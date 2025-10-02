package ru.yandex.practicum.shoppingcart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.client.ShoppingCartClient;
import ru.yandex.practicum.shoppingcart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.shoppingcart.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService service;

    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        log.info("GET /api/v1/shopping-cart username={}", username);
        return service.getCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> productsToAdd) {
        log.info("PUT /api/v1/shopping-cart username={} products={}", username, productsToAdd);
        return service.addProducts(username, productsToAdd);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username,
                                          @RequestBody List<UUID> productIds) {
        log.info("POST /api/v1/shopping-cart/remove username={} productIds={}", username, productIds);
        return service.removeProducts(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request) {
        log.info("POST /api/v1/shopping-cart/change-quantity username={} request={}", username, request);
        return service.changeQuantity(username, request);
    }

    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        log.info("DELETE /api/v1/shopping-cart username={}", username);
        service.deactivateCart(username);
    }
}
