package ru.yandex.practicum.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.common.dto.OrderDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody OrderDto dto);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto dto);

    @PostMapping("/picked")
    DeliveryDto markPicked(@RequestBody UUID orderId);

    @PostMapping("/successful")
    DeliveryDto markDelivered(@RequestBody UUID orderId);

    @PostMapping("/failed")
    DeliveryDto markFailed(@RequestBody UUID orderId);
}