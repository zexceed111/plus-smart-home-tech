package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.DuplicateProductException;
import ru.yandex.practicum.exception.NotEnoughProductException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    @Transactional
    public void newProductInWarehouse(AddNewProductInWarehouseRequest request) {
        checkProductAlreadyExist(request.getProductId());
        WarehouseProduct warehouseProduct = WarehouseMapper.toWarehouseProduct(request);
        warehouseRepository.save(warehouseProduct);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        Map<UUID, Long> cartProductsMap = cart.getProducts();
        List<WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(cartProductsMap.keySet());
        Map<UUID, Long> warehouseProductsMap = warehouseProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, WarehouseProduct::getQuantity));

        List<String> exceptionMessages = cartProductsMap.entrySet()
                .stream()
                .filter(entry -> warehouseProductsMap.getOrDefault(entry.getKey(), 0L) < entry.getValue())
                .map(entry -> String.format(
                        "Недостаток товара с id = %s, не хватает %d шт.%n",
                        entry.getKey(),
                        entry.getValue() - warehouseProductsMap.getOrDefault(entry.getKey(), 0L)
                ))
                .toList();

        if (!exceptionMessages.isEmpty()) {
            throw new NotEnoughProductException(exceptionMessages.toString());
        }

        return calculateDeliveryParams(warehouseProducts);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = checkWarehouseProductExist(request.getProductId());
        Long newQuantity = warehouseProduct.getQuantity() + request.getQuantity();
        warehouseProduct.setQuantity(newQuantity);
        warehouseRepository.save(warehouseProduct);
        updateProductQuantityInShoppingStore(warehouseProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(CURRENT_ADDRESS);
        addressDto.setCity(CURRENT_ADDRESS);
        addressDto.setStreet(CURRENT_ADDRESS);
        addressDto.setHouse(CURRENT_ADDRESS);
        addressDto.setFlat(CURRENT_ADDRESS);
        return addressDto;
    }

    private void checkProductAlreadyExist(UUID productId) {
        warehouseRepository.findById(productId).ifPresent(product ->
                {
                    throw new DuplicateProductException("Товар с id = " + productId + " уже представлен на складе");
                }
        );
    }

    private BookedProductsDto calculateDeliveryParams(List<WarehouseProduct> warehouseProducts) {

        BookedProductsDto bookedProductsDto = new BookedProductsDto();

        double totalWeight = warehouseProducts.stream()
                .mapToDouble(WarehouseProduct::getWeight)
                .sum();

        double totalVolume = warehouseProducts.stream()
                .mapToDouble(product -> product.getWidth() * product.getHeight() * product.getDepth())
                .sum();

        boolean hasFragileProducts = warehouseProducts.stream()
                .anyMatch(WarehouseProduct::isFragile);

        bookedProductsDto.setDeliveryWeight(totalWeight);
        bookedProductsDto.setDeliveryVolume(totalVolume);
        bookedProductsDto.setFragile(hasFragileProducts);

        return bookedProductsDto;
    }

    private WarehouseProduct checkWarehouseProductExist(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Товар с id = " + productId + " отсутствует на складе"));
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        Long quantity = product.getQuantity();
        QuantityState quantityState;

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.FEW;
        } else if (quantity < 100 && quantity > 10) {
            quantityState = QuantityState.ENOUGH;
        } else {
            quantityState = QuantityState.MANY;
        }

        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest();
        request.setProductId(product.getProductId());
        request.setQuantityState(quantityState);

        shoppingStoreClient.setProductQuantityState(request);
    }
}
