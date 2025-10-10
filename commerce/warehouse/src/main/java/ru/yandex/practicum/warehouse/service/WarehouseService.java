package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.common.dto.AssemblyRequest;
import ru.yandex.practicum.common.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.ReturnRequest;
import ru.yandex.practicum.warehouse.dto.ShipmentRequest;
import ru.yandex.practicum.warehouse.dto.ShoppingCartDto;

public interface WarehouseService {

    void registerNewProduct(NewProductInWarehouseRequest request);

    void addProductQuantity(AddProductToWarehouseRequest request);

    BookedProductsDto checkAvailabilityAndBook(ShoppingCartDto cart);

    AddressDto getWarehouseAddress();

    BookedProductsDto assembleProducts(AssemblyRequest request);

    void markAsShipped(ShipmentRequest request);

    void returnProducts(ReturnRequest request);
}
