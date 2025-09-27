package ru.yandex.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {
    @PostMapping("/api/v1/shopping-store/quantityState")
    void setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) throws FeignException;
}
