package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${ShoppingStore.api.prefix}")
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @Value("${ShoppingStore.api.prefix}")
    private String prefix;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createNewProduct(@Valid @RequestBody NewProductRequest newProductRequest) {
        log.info("Поступил запрос POST {} на создание Product с телом {}", prefix, newProductRequest);
        ProductDto response = productService.createNewProduct(newProductRequest);
        log.info("Сформирован ответ POST {} с телом: {}", prefix, response);
        return response;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        log.info("Поступил запрос GET {} на получение продуктов: category = {}, pageable = {}", prefix, category, pageable);
        Page<ProductDto> response = productService.getProductsByParams(category, pageable);
        log.info("Сформирован ответ GET {} с телом: {}", prefix, response);
        return response;
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@Valid @RequestBody UpdateProductRequest updateProductRequest) {
        log.info("Поступил запрос POST {}/update на обновление Product с телом {}", prefix, updateProductRequest);
        ProductDto response = productService.updateProduct(updateProductRequest);
        log.info("Сформирован ответ POST {}/update с телом: {}", prefix, response);
        return response;
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public void removeProductFromStore(@RequestBody UUID id) {
        log.info("Поступил запрос POST {}/removeProductFromStore на удаление Product с id = {}", prefix, id);
        productService.removeProductFromStore(id);
        log.info("Выполнен запрос POST {}/removeProductFromStore для id = {}", prefix, id);
    }

    @Override
    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public void setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        log.info("Поступил запрос POST {}/quantityState с телом = {}", prefix, request);
        productService.setProductQuantityState(request);
        log.info("Выполнен запрос POST {}/quantityState для {}", prefix, request);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(@PathVariable UUID productId) {
        log.info("Поступил запрос GET {}/{} на получение ProductDto", prefix, productId);
        ProductDto response = productService.getProduct(productId);
        log.info("Сформирован ответ GET {}/{} с телом: {}", prefix, productId, response);
        return response;
    }
}
