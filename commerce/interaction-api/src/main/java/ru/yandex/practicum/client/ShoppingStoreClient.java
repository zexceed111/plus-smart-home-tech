package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {
    String BASE_PATH = "";
    String REMOVE_PATH = "/removeProductFromStore";
    String QUANTITY_STATE_PATH = "/quantityState";

    @GetMapping(BASE_PATH)
    ProductsDto getProducts(@RequestParam @NotNull ProductCategory category, Pageable pageable);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PutMapping(BASE_PATH)
    ProductDto createProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping(BASE_PATH)
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping(REMOVE_PATH)
    void removeProduct(@RequestBody UUID productId);

    @PostMapping(QUANTITY_STATE_PATH)
    ProductDto updateQuantityState(@PathVariable UUID productId,
                                   @PathVariable QuantityState quantity);
}