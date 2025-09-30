package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.model.Product;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    ProductsDto getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto updateQuantityState(UUID productId, QuantityState quantityState);

    boolean removeProduct(UUID productId);
}
