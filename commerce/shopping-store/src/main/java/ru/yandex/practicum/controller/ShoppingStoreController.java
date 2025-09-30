package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.NotFoundException;
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

    @PostMapping("/quantityState")
    public ResponseEntity<?> setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        try {
            productService.setProductQuantityState(request);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiError(HttpStatus.NOT_FOUND, e, "Товар не найден"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, "Неизвестная ошибка"));
        }
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
