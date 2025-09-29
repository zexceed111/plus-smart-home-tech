package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${ShoppingStore.api.prefix}")
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ProductService productService;

    @Value("${ShoppingStore.api.prefix}")
    private String prefix;

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createNewProduct(@Valid @RequestBody NewProductRequest newProductRequest) {
        log.info("Поступил запрос Put {} на создание Product с телом {}", prefix, newProductRequest);
        ProductDto response = productService.createNewProduct(newProductRequest);
        log.info("Сформирован ответ Put {} с телом: {}", prefix, response);
        return response;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getProducts(@RequestParam(required = true) ProductCategory category,
                                        Pageable pageable) {
        log.info("Поступил запрос Get {} на получение List<ProductDto> с параметрами category = {}, pageable = {}", prefix, category, pageable);
        List<ProductDto> response = productService.getProductsByParams(category, pageable);
        log.info("Сформирован ответ Get {} с телом: {}", prefix, response);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@Valid @RequestBody UpdateProductRequest updateProductRequest) {
        log.info("Поступил запрос Post {} на обновление Product с телом {}", prefix, updateProductRequest);
        ProductDto response = productService.updateProduct(updateProductRequest);
        log.info("Сформирован ответ Post {} с телом: {}", prefix, response);
        return response;
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public void removeProductFromStore(@RequestBody UUID id) {
        log.info("Поступил запрос Post {}/removeProductFromStore на удаление (деактивацию) Product с id = {}", prefix, id);
        productService.removeProductFromStore(id);
        log.info("Выполнен запрос Post {}/removeProductFromStore на удаление (деактивацию) Product с id = {}", prefix, id);
    }

    @Override
    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public void setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        log.info("Поступил запрос Post {}/quantityState на изменение количества Product с телом = {}", prefix, request);
        productService.setProductQuantityState(request);
        log.info("Выполнен запрос Post {}/quantityState на изменение количества Product с телом = {}", prefix, request);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProducts(@PathVariable UUID productId) {
        log.info("Поступил запрос Get {}/{} на получение ProductDto с id = {}", prefix, productId, productId);
        ProductDto response = productService.getProduct(productId);
        log.info("Сформирован ответ Get {}/{} с телом: {}", prefix, productId, response);
        return response;
    }
}
