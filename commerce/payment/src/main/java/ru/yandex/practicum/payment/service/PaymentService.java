package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.client.OrderClient;
import ru.yandex.practicum.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.common.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.entity.PaymentEntity;
import ru.yandex.practicum.payment.entity.PaymentStatus;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderClient orderClient;
    private final ShoppingStoreClient storeClient;

    // Вычисление общей стоимости всех продуктов в заказе
    public BigDecimal calculateProductCost(OrderDto order) {
        return order.getProducts().entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();  // Получаем ID продукта
                    int quantity = entry.getValue();  // Получаем количество продукта
                    BigDecimal price = storeClient.getProduct(productId).getPrice();  // Получаем цену продукта
                    return price.multiply(BigDecimal.valueOf(quantity));  // Умножаем цену на количество, чтобы получить стоимость этого продукта
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Суммируем стоимость всех продуктов
    }

    // Вычисление общей стоимости заказа с учетом НДС и доставки
    public BigDecimal calculateTotalCost(OrderDto order) {
        BigDecimal productCost = calculateProductCost(order);  // Вычисляем стоимость продуктов
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));  // Вычисляем НДС (10% от стоимости продуктов)
        BigDecimal delivery = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.valueOf(50);  // Используем указанную цену доставки или значение по умолчанию (50)

        return productCost.add(vat).add(delivery);  // Возвращаем сумму стоимости продуктов, НДС и доставки
    }

    @Transactional
    public PaymentDto createPayment(OrderDto order) {
        // Вычисляем стоимость продуктов, НДС и общую стоимость для заказа
        BigDecimal productCost = calculateProductCost(order);
        BigDecimal delivery = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.valueOf(50);
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));
        BigDecimal total = productCost.add(vat).add(delivery);

        // Создаем новую сущность платежа с вычисленными стоимостями
        PaymentEntity entity = PaymentEntity.builder()
                .orderId(order.getOrderId())
                .productCost(productCost)
                .deliveryCost(delivery)
                .totalCost(total)
                .status(PaymentStatus.PENDING)  // Статус по умолчанию — PENDING
                .build();

        // Сохраняем сущность платежа в репозитории и возвращаем её в виде DTO
        return mapper.toDto(repository.save(entity));
    }

    // Переносим логику взаимодействия с другими сервисами вне транзакции

    public void markSuccess(UUID paymentId) {
        // Сначала выполняем все операции с базой данных внутри транзакции
        PaymentEntity payment = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Платеж не найден"));

        // Обновляем статус платежа на SUCCESS
        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);  // Сохраняем обновленную сущность платежа

        // После того как транзакция завершена, выходим и выполняем вызов внешнего сервиса
        // (позволяет избежать отката в случае ошибки HTTP)
        orderClient.paymentSuccess(payment.getOrderId());
    }

    public void markFailed(UUID paymentId) {
        // Сначала выполняем все операции с базой данных внутри транзакции
        PaymentEntity payment = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Платеж не найден"));

        // Обновляем статус платежа на FAILED
        payment.setStatus(PaymentStatus.FAILED);
        repository.save(payment);  // Сохраняем обновленную сущность платежа

        // После того как транзакция завершена, выходим и выполняем вызов внешнего сервиса
        // (позволяет избежать отката в случае ошибки HTTP)
        orderClient.paymentFailed(payment.getOrderId());
    }
}
