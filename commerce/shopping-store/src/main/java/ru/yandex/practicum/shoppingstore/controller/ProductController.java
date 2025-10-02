package ru.yandex.practicum.shoppingstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.shoppingstore.dto.ProductDto;
import ru.yandex.practicum.shoppingstore.model.ProductCategory;
import ru.yandex.practicum.shoppingstore.model.QuantityState;
import ru.yandex.practicum.shoppingstore.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingstore.service.ProductService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController implements ShoppingStoreClient {

    private final ProductService service;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("GET /api/v1/shopping-store?category={} page={}", category, pageable);
        return service.getProductsByCategory(category, pageable);
    }

    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto dto) {
        log.info("PUT /api/v1/shopping-store body={}", dto);
        return service.createProduct(dto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto dto) {
        log.info("POST /api/v1/shopping-store body={}", dto);
        return service.updateProduct(dto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore id={}", productId);
        return service.removeProductFromStore(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam String quantityState) {
        log.info("POST /api/v1/shopping-store/quantityState id={} state={}", productId, quantityState);
        return service.updateProductQuantityState(
                new SetProductQuantityStateRequest(productId, QuantityState.valueOf(quantityState))
        );
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        log.info("GET /api/v1/shopping-store/{}", productId);
        return service.getProductById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}