package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.DeactivatedCartException;
import ru.yandex.practicum.exception.UnauthorizedUserException;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartState;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return ShoppingCartMapper.toShoppingCartDto(getShoppingCartOrCreate(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> newProducts) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getShoppingCartOrCreate(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Long> products = shoppingCart.getProducts();
        products.putAll(newProducts);
        shoppingCart.setProducts(products);
        ShoppingCartDto shoppingCartDto = ShoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
        warehouseClient.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
        return shoppingCartDto;
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getShoppingCartOrCreate(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getShoppingCartOrCreate(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Long> products = shoppingCart.getProducts();
        productIds.forEach(products::remove);
        shoppingCart.setProducts(products);
        return ShoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getShoppingCartOrCreate(username);
        checkCartIsActive(shoppingCart);
        Map<UUID, Long> products = shoppingCart.getProducts();
        products.put(request.getProductId(), request.getNewQuantity());
        shoppingCart.setProducts(products);
        ShoppingCartDto shoppingCartDto = ShoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart));
        warehouseClient.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
        return shoppingCartDto;
    }

    private ShoppingCart getShoppingCartOrCreate(String username) {
        return shoppingCartRepository.findByUserName(username).orElseGet(() ->
                {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setUserName(username);
                    shoppingCart.setState(ShoppingCartState.ACTIVE);
                    shoppingCart.setProducts(new HashMap<>());
                    return shoppingCartRepository.save(shoppingCart);
                }
        );
    }

    private void checkUserAuthorization(String username) {
        if (username == null || username.isBlank()) {
            throw new UnauthorizedUserException("Пользователь " + username + " не авторизован");
        }
    }

    private void checkCartIsActive(ShoppingCart shoppingCart) {
        if (shoppingCart.getState() == ShoppingCartState.DEACTIVATE) {
            throw new DeactivatedCartException("Корзина пользователя " + shoppingCart.getUserName() + " деактивирована");
        }
    }
}
