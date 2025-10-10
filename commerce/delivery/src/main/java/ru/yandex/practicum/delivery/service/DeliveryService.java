package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.warehouse.dto.ShipmentRequest;
import ru.yandex.practicum.delivery.entity.DeliveryEntity;
import ru.yandex.practicum.delivery.entity.DeliveryStatus;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.client.OrderClient;
import ru.yandex.practicum.interaction.client.WarehouseClient;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto dto) {
        DeliveryEntity entity = mapper.fromDto(dto);
        entity.setDeliveryStatus(DeliveryStatus.CREATED);
        return mapper.toDto(repository.save(entity));
    }

    public BigDecimal calculateCost(DeliveryDto dto) {
        double cost = 5.0;

        if (dto.getFromAddress().getStreet().contains("ADDRESS_2")) {
            cost = cost * 2 + 5;
        } else if (dto.getFromAddress().getStreet().contains("ADDRESS_1")) {
            cost = cost * 1 + 5;
        }

        if (dto.isFragile()) {
            cost += cost * 0.2;
        }

        cost += dto.getWeight() * 0.3;
        cost += dto.getVolume() * 0.2;

        if (!dto.getFromAddress().getStreet().equalsIgnoreCase(dto.getToAddress().getStreet())) {
            cost += cost * 0.2;
        }

        return BigDecimal.valueOf(Math.round(cost * 100.0) / 100.0);
    }

    @Transactional
    public DeliveryDto markPicked(UUID orderId) {
        DeliveryEntity delivery = getByOrder(orderId);
        delivery.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);

        orderClient.delivery(orderId);
        warehouseClient.shippedToDelivery(new ShipmentRequest(orderId, delivery.getDeliveryId()));

        return mapper.toDto(repository.save(delivery));
    }

    @Transactional
    public DeliveryDto markDelivered(UUID orderId) {
        DeliveryEntity delivery = getByOrder(orderId);
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);

        orderClient.delivery(orderId);

        return mapper.toDto(repository.save(delivery));
    }

    @Transactional
    public DeliveryDto markFailed(UUID orderId) {
        DeliveryEntity delivery = getByOrder(orderId);
        delivery.setDeliveryStatus(DeliveryStatus.FAILED);

        orderClient.deliveryFailed(orderId);

        return mapper.toDto(repository.save(delivery));
    }

    private DeliveryEntity getByOrder(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found for order: " + orderId));
    }
}
