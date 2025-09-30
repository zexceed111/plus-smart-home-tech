package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NoProductsException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getCart(String userName) {
        ShoppingCart cartOrThrow = getCartOrThrow(userName);
        ShoppingCartDto map = shoppingCartMapper.map(cartOrThrow);
        return map;
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String userName, Map<UUID, Long> products) {
        ShoppingCart cart = shoppingCartRepository.findByUserName(userName)
                .orElseGet(() -> createNewCart(userName));

        Map<UUID, Long> mergedProducts = Optional.ofNullable(cart.getItems())
                .map(HashMap::new)
                .orElseGet(HashMap::new);

        mergedProducts.putAll(products);
        cart.setItems(mergedProducts);

        return shoppingCartMapper.map(shoppingCartRepository.save(cart));
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String userName, List<UUID> productIds) {
        ShoppingCart cart = getCartOrThrow(userName);
        productIds.forEach(cart.getItems()::remove);
        return shoppingCartMapper.map(shoppingCartRepository.save(cart));
    }

    @Override
    @Transactional
    public ShoppingCartDto updateQuantity(String userName, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getCartOrThrow(userName);

        if (!cart.getItems().containsKey(request.getProductId())) {
            throw new NoProductsException("Product not found in cart");
        }

        cart.getItems().compute(request.getProductId(),
                (k, v) -> request.getNewQuantity());

        return shoppingCartMapper.map(shoppingCartRepository.save(cart));
    }

    @Override
    @Transactional
    public BookedProductsDto bookProducts(String userName) {
        ShoppingCartDto cartDto = shoppingCartMapper.map(getCartOrThrow(userName));
        return warehouseClient.checkProductQuantityEnoughForShoppingCart(cartDto);
    }

    @Override
    @Transactional
    public void clearCart(String userName) {
        if (!shoppingCartRepository.existsByUserName(userName)) {
            throw new NotFoundException("Cart not found");
        }
        shoppingCartRepository.deleteByUserName(userName);
    }

    private ShoppingCart getCartOrThrow(String userName) {
        return shoppingCartRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userName));
    }

    private ShoppingCart createNewCart(String userName) {
        return ShoppingCart.builder()
                .userName(userName)
                .items(new HashMap<>())
                .build();
    }
}