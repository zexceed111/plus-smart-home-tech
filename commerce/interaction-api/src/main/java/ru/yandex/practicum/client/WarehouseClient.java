package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseClient {
    String CHECK_ENDPOINT = "/api/v1/warehouse/check";
    String ADD_ENDPOINT = "/add";
    String ADDRESS_ENDPOINT = "/address";

    @PutMapping
    void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping(CHECK_ENDPOINT)
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping(ADD_ENDPOINT)
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request);

    @GetMapping(ADDRESS_ENDPOINT)
    AddressDto getWarehouseAddress();
}