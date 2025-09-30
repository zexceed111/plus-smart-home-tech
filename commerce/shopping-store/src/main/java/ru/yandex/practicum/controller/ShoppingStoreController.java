package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    @Override
    public ProductsDto getProducts(
            @RequestParam @NotNull ProductCategory category, Pageable pageable) {
        return shoppingStoreService.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    @Override
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProductById(productId);
    }

    @PutMapping
    @Override
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createProduct(productDto);
    }

    @PostMapping
    @Override
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    @DeleteMapping("/{productId}")
    public void removeProduct(@PathVariable UUID productId) {
        shoppingStoreService.removeProduct(productId);
    }

    @PostMapping("/removeProductFromStore")
    public void removeProductFromStore(@RequestBody UUID productId) {
        shoppingStoreService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    @Override
    public ProductDto updateQuantityState(@RequestParam UUID productId,
                                          @RequestParam QuantityState quantityState) {
        return shoppingStoreService.updateQuantityState(productId, quantityState);
    }
}