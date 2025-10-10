package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction.client.DeliveryClient;
import ru.yandex.practicum.interaction.client.PaymentClient;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.common.dto.BookedProductsDto;
import ru.yandex.practicum.common.dto.OrderDto;
import ru.yandex.practicum.order.entity.OrderEntity;
import ru.yandex.practicum.order.entity.OrderState;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.repository.OrderRepository;
import ru.yandex.practicum.common.dto.AssemblyRequest;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        OrderEntity entity = mapper.fromDto(dto);
        entity.setState(OrderState.NEW);
        OrderEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByCartId(UUID cartId) {
        return repository.findAllByShoppingCartId(cartId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getById(UUID id) {
        return repository.findById(id).map(mapper::toDto).orElse(null);
    }

    @Transactional
    public OrderDto markAsAssembled(UUID orderId) {
        OrderEntity order = getOrder(orderId);

        BookedProductsDto booked = warehouseClient.assemblyProductForOrderFromShoppingCart
                (new AssemblyRequest(orderId, order.getProducts()));

        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markAssemblyFailed(UUID orderId) {
        OrderEntity order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markAsPaid(UUID orderId) {
        OrderEntity order = getOrder(orderId);

        PaymentDto result = paymentClient.payment(mapper.toDto(order));
        order.setPaymentId(result.getPaymentId());
        order.setState(OrderState.PAID);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markPaymentFailed(UUID orderId) {
        OrderEntity order = getOrder(orderId);
        paymentClient.paymentFailed(order.getPaymentId());
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markAsDelivered(UUID orderId) {
        OrderEntity order = getOrder(orderId);

        DeliveryDto deliveryResult = deliveryClient.planDelivery(mapper.toDto(order));
        order.setDeliveryId(deliveryResult.getDeliveryId());
        order.setState(OrderState.DELIVERED);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markDeliveryFailed(UUID orderId) {
        OrderEntity order = getOrder(orderId);
        deliveryClient.markFailed(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto markAsCompleted(UUID orderId) {
        return mapper.toDto(changeStatus(orderId, OrderState.COMPLETED));
    }

    @Transactional
    public OrderDto markAsReturned(UUID orderId) {
        return mapper.toDto(changeStatus(orderId, OrderState.PRODUCT_RETURNED));
    }

    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        OrderEntity order = getOrder(orderId);
        BigDecimal cost = deliveryClient.deliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(cost);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        OrderEntity order = getOrder(orderId);

        BigDecimal productPrice = paymentClient.productCost(mapper.toDto(order));
        BigDecimal total = paymentClient.getTotalCost(mapper.toDto(order));

        order.setProductPrice(productPrice);
        order.setTotalPrice(total);

        return mapper.toDto(repository.save(order));
    }

    private OrderEntity changeStatus(UUID orderId, OrderState state) {
        OrderEntity order = getOrder(orderId);
        order.setState(state);
        return repository.save(order);
    }

    private OrderEntity getOrder(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}
