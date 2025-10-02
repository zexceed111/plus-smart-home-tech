package ru.yandex.practicum.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.shoppingcart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.shoppingcart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam String username, @RequestBody Map<UUID, Long> productsToAdd);

    @PostMapping("/remove")
    ShoppingCartDto removeProducts(@RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);

    @DeleteMapping
    void deactivateCart(@RequestParam String username);
}