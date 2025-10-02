package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService service;

    @PutMapping
    public void registerNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        log.info("Register new product on warehouse: productId={}, fragile={}, weight={}, dimension={}x{}x{}",
                request.getProductId(),
                request.isFragile(),
                request.getWeight(),
                request.getDimension() != null ? request.getDimension().getWidth() : null,
                request.getDimension() != null ? request.getDimension().getHeight() : null,
                request.getDimension() != null ? request.getDimension().getDepth() : null
        );
        service.registerNewProduct(request);
        log.info("Registered product: productId={}", request.getProductId());
    }

    @PostMapping("/add")
    public void addProductQuantity(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Add product quantity: productId={}, quantity={}", request.getProductId(), request.getQuantity());
        service.addProductQuantity(request);
        log.info("Quantity added: productId={}, quantity={}", request.getProductId(), request.getQuantity());
    }

    @PostMapping("/check")
    public BookedProductsDto checkAvailability(@RequestBody ShoppingCartDto cart) {
        log.info("Check availability & book: cartId={}, itemsCount={}",
                cart.getShoppingCartId(),
                cart.getProducts() != null ? cart.getProducts().size() : 0
        );
        BookedProductsDto result = service.checkAvailabilityAndBook(cart);
        log.info("Booked: cartId={}, deliveryWeight={}, deliveryVolume={}, fragile={}",
                cart.getShoppingCartId(),
                result.getDeliveryWeight(),
                result.getDeliveryVolume(),
                result.isFragile()
        );
        return result;
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Get warehouse address");
        AddressDto address = service.getWarehouseAddress();
        log.info("Warehouse address resolved: country={}, city={}, street={}, house={}, flat={}",
                address.getCountry(), address.getCity(), address.getStreet(), address.getHouse(), address.getFlat());
        return address;
    }
}
