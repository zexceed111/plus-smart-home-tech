package ru.yandex.practicum.warehouse.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.common.dto.AssemblyRequest;
import ru.yandex.practicum.common.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.DimensionDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.OrderBooking;
import ru.yandex.practicum.warehouse.dto.ReturnRequest;
import ru.yandex.practicum.warehouse.dto.ShipmentRequest;
import ru.yandex.practicum.warehouse.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository repository;
    private final Map<UUID, OrderBooking> bookings = new HashMap<>();

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static String currentAddress;
    private static final SecureRandom RANDOM = new SecureRandom();

    @PostConstruct
    public void init() {
        int i = RANDOM.nextInt(ADDRESSES.length);
        currentAddress = ADDRESSES[i];
    }

    @Override
    @Transactional
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            throw new IllegalStateException("Product already exists on warehouse");
        }

        DimensionDto dd = request.getDimension();
        Dimension dimension = dd == null ? null : Dimension.builder()
                .width(dd.getWidth())
                .height(dd.getHeight())
                .depth(dd.getDepth())
                .build();

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(request.getProductId())
                .fragile(request.isFragile())
                .dimension(dimension)
                .weight(request.getWeight())
                .quantity(0L)
                .build();

        repository.save(product);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Override
    @Transactional
    public BookedProductsDto checkAvailabilityAndBook(ShoppingCartDto cart) {
        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : cart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            long quantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found"));

            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Not enough quantity for product " + productId);
            }

            product.setQuantity(product.getQuantity() - quantity);
            repository.save(product);

            totalWeight += product.getWeight() * quantity;

            Dimension d = product.getDimension();
            totalVolume += (d.getDepth() * d.getHeight() * d.getWidth()) * quantity;

            if (product.isFragile()) {
                hasFragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return new AddressDto(currentAddress, currentAddress, currentAddress, currentAddress, currentAddress);
    }

    @Override
    @Transactional
    public BookedProductsDto assembleProducts(AssemblyRequest request) {
        Map<UUID, Long> converted = request.getProducts().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().longValue()
                ));

        ShoppingCartDto cart = new ShoppingCartDto();
        cart.setProducts(converted);

        BookedProductsDto booked = checkAvailabilityAndBook(cart);

        bookings.put(request.getOrderId(), new OrderBooking(request.getOrderId(), null, converted));

        return booked;
    }

    @Override
    @Transactional
    public void markAsShipped(ShipmentRequest request) {
        OrderBooking booking = bookings.get(request.getOrderId());
        if (booking == null) {
            throw new IllegalArgumentException("No booking found for order " + request.getOrderId());
        }
        booking.setDeliveryId(request.getDeliveryId());
        System.out.println("Order " + request.getOrderId() + " marked as shipped with delivery " + request.getDeliveryId());
    }

    @Override
    @Transactional
    public void returnProducts(ReturnRequest request) {
        for (Map.Entry<UUID, Integer> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + entry.getKey()));
            product.setQuantity(product.getQuantity() + entry.getValue());
            repository.save(product);
        }
        System.out.println("Products returned for order: " + request.getOrderId());
    }
}
