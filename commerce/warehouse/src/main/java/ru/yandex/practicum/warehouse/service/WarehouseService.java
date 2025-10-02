package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.ShoppingCartDto;

public interface WarehouseService {

    void registerNewProduct(NewProductInWarehouseRequest request);

    void addProductQuantity(AddProductToWarehouseRequest request);

    BookedProductsDto checkAvailabilityAndBook(ShoppingCartDto cart);

    AddressDto getWarehouseAddress();
}
