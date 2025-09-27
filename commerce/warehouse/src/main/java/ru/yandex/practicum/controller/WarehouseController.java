package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${Warehouse.api.prefix}")
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Value("${Warehouse.api.prefix}")
    private String prefix;

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void newProductInWarehouse(@RequestBody @Valid AddNewProductInWarehouseRequest request) {
        log.info("Поступил запрос Put {} на добавление нового товара с телом {}", prefix, request);
        warehouseService.newProductInWarehouse(request);
        log.info("Обработан запрос Put {} на добавление нового товара с телом {}", prefix, request);
    }

    @Override
    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto cart) {
        log.info("Поступил запрос Post {}/check на проверку остатка на складе товаров: {}", prefix, cart);
        BookedProductsDto response = warehouseService.checkProductQuantityEnoughForShoppingCart(cart);
        log.info("Сформирован ответ Post {}/check с телом: {}", prefix, response);
        return response;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request) {
        log.info("Поступил запрос Post {}/add на увеличение остатка товаров: {}", prefix, request);
        warehouseService.addProductToWarehouse(request);
        log.info("Обработан запрос Post {}/add на увеличение остатка товаров: {}", prefix, request);
    }

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("Поступил запрос Get {}/address на получение адреса", prefix);
        AddressDto response = warehouseService.getWarehouseAddress();
        log.info("Сформирован ответ Get {}/address с телом: {}", prefix, response);
        return response;
    }

}
