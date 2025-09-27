package ru.yandex.practicum.feign;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.ServiceNotAvailableException;

@Component
@Slf4j
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) throws FeignException {
        log.error("Сервис warehouse недоступен. Повторите запрос позже.");
        throw new ServiceNotAvailableException("Сервис warehouse недоступен. Повторить запрос позже");
    }
}
