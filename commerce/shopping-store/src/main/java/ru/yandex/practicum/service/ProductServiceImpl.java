package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductDto createNewProduct(NewProductRequest newProductRequest) {
        Product product = ProductMapper.toProduct(newProductRequest);
        return ProductMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public List<ProductDto> getProductsByParams(ProductCategory category, Pageable pageable) {
        List<Product> products = productRepository.findAllByProductCategory(category, pageable);
        return ProductMapper.toProductDto(products);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(UpdateProductRequest updateProductRequest) {
        UUID id = updateProductRequest.getProductId();
        Product existProduct = checkProductExist(id);

        if (updateProductRequest.getImageSrc() == null || updateProductRequest.getImageSrc().isBlank()) {
            updateProductRequest.setImageSrc(existProduct.getImageSrc());
        }

        if (updateProductRequest.getProductCategory() == null) {
            updateProductRequest.setProductCategory(existProduct.getProductCategory());
        }

        Product updatedProduct = ProductMapper.toProduct(updateProductRequest);

        return ProductMapper.toProductDto(productRepository.save(updatedProduct));
    }

    @Transactional
    @Override
    public void removeProductFromStore(UUID id) {
        Product existProduct = checkProductExist(id);
        existProduct.setProductState(ProductState.DEACTIVATE);
        productRepository.save(existProduct);
    }

    @Transactional
    @Override
    public void setProductQuantityState(SetProductQuantityStateRequest request) {
        UUID id = request.getProductId();
        QuantityState quantityState = request.getQuantityState();
        Product existProduct = checkProductExist(id);
        existProduct.setQuantityState(quantityState);
        productRepository.save(existProduct);
    }

    @Override
    public ProductDto getProduct(UUID id) {
        return ProductMapper.toProductDto(checkProductExist(id));
    }

    private Product checkProductExist(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукта с id = " + id + " не существует"));
    }
}
