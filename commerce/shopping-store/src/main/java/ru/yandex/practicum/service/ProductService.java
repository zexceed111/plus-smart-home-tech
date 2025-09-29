package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.*;

import java.util.UUID;

public interface ProductService {

    ProductDto createNewProduct(NewProductRequest newProductRequest);

    Page<ProductDto> getProductsByParams(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(UpdateProductRequest updateProductRequest);

    void removeProductFromStore(UUID id);

    void setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto getProduct(UUID id);
}
