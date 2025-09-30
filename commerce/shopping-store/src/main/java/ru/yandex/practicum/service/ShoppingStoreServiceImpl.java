package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductsDto getProducts(ProductCategory category, Pageable pageable) {

        PageRequest pageRequest = pageable.toPageRequest();

        List<ProductDto> list = productRepository.findAllByProductCategory(category, pageRequest)
                .stream()
                .map(productMapper::map)
                .toList();

        ProductsDto result = new ProductsDto();
        result.setContent(list);
        List<SortInfo> sortInfoList = pageRequest.getSort().stream()
                .map(order -> new SortInfo(order.getProperty(), order.getDirection().name()))
                .collect(Collectors.toList());
        result.setSort(sortInfoList);

        return result;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return productMapper.map(getProduct(productId));
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.map(productDto);

        return productMapper.map(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = getProductByProductName(productDto.getProductName());
        if (product == null) {
            Product newProduct = productMapper.map(productDto);
            Product save = productRepository.save(newProduct);
            return productMapper.map(save);
        }
        productMapper.update(productDto, product);
        return productMapper.map(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = getProduct(productId);
        product.setQuantityState(quantityState);
        Product saveProduct = productRepository.save(product);
        ProductDto dto = productMapper.map(saveProduct);

        return dto;
    }

    @Override
    @Transactional
    public boolean removeProduct(UUID productId) {
        Product product = getProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);

        return true;
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException(
                String.format("Ошибка, товар по идентификатору %s в БД не найден", productId))
        );
    }

    private Product getProductByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }
}
