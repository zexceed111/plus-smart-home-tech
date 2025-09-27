package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${ShoppingCart.api.prefix}")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Value("${ShoppingCart.api.prefix}")
    private String prefix;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getShoppingCarts(@RequestParam String username) {
        log.info("Поступил запрос Get {} на получение ShoppingCartDto пользователя {}", prefix, username);
        ShoppingCartDto response = shoppingCartService.getShoppingCart(username);
        log.info("Сформирован ответ Get {} с телом: {}", prefix, response);
        return response;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Long> products) {
        log.info("Поступил запрос Put {} на добавление в корзину пользователя {} товаров: {}", prefix, username, products);
        ShoppingCartDto response = shoppingCartService.addProductToShoppingCart(username, products);
        log.info("Сформирован ответ Put {} с телом: {}", prefix, response);
        return response;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deactivateShoppingCart(@RequestParam String username) {
        log.info("Поступил запрос Delete {} на деактивацию корзины пользователя {}", prefix, username);
        shoppingCartService.deactivateCurrentShoppingCart(username);
        log.info("Обработан запрос Delete {} на деактивацию корзины пользователя {}", prefix, username);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeProductFromShoppingCart(@RequestParam String username, @RequestBody List<UUID> productIds) {
        log.info("Поступил запрос Post {}/remove на удаление из корзины пользователя {} товаров с UUID: {}", prefix, username, productIds);
        ShoppingCartDto response = shoppingCartService.removeFromShoppingCart(username, productIds);
        log.info("Сформирован ответ Post {}/remove с телом: {}", prefix, response);
        return response;
    }

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        log.info("Поступил запрос Post {}/change-quantity на изменение кол-ва товаров с UUID = {} - {} шт. в корзине пользователя {}",
                prefix, request.getProductId(), request.getQuantity(), username);
        ShoppingCartDto response = shoppingCartService.changeProductQuantity(username, request);
        log.info("Сформирован ответ Post {}/change-quantity с телом: {}", prefix, response);
        return response;
    }
}
