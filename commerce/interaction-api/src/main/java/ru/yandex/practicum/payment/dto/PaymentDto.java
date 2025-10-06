package ru.yandex.practicum.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal productCost;
    private BigDecimal deliveryCost;
    private BigDecimal totalCost;
    private String status;
}
