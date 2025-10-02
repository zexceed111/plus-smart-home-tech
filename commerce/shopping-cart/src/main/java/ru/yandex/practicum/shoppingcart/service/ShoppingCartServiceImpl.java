package ru.yandex.practicum.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.shoppingcart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.shoppingcart.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.shoppingcart.model.ShoppingCartState;
import ru.yandex.practicum.shoppingcart.repository.ShoppingCartRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getCart(String username) {
        return toDto(findOrCreateCart(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> productsToAdd) {
        ShoppingCart cart = findOrCreateCart(username);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart is deactivated");
        }

        Map<UUID, Long> merged = new HashMap<>(cart.getProducts());
        productsToAdd.forEach((id, qty) -> merged.merge(id, qty, Long::sum));

        ru.yandex.practicum.warehouse.dto.ShoppingCartDto toBook =
                ru.yandex.practicum.warehouse.dto.ShoppingCartDto.builder()
                        .shoppingCartId(cart.getShoppingCartId())
                        .products(productsToAdd)
                        .build();

        BookedProductsDto booked = warehouseClient.checkAvailability(toBook);

        cart.setProducts(merged);
        repository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        ShoppingCart cart = findOrCreateCart(username);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart is deactivated");
        }
        productIds.forEach(cart.getProducts()::remove);
        repository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = findOrCreateCart(username);
        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            throw new IllegalStateException("Cart is deactivated");
        }
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new IllegalArgumentException("Product not found in cart");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        repository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        ShoppingCart cart = findOrCreateCart(username);
        cart.setState(ShoppingCartState.DEACTIVATED);
        repository.save(cart);
    }

    private ShoppingCart findOrCreateCart(String username) {
        return repository.findByUsername(username).orElseGet(() -> {
            ShoppingCart cart = ShoppingCart.builder()
                    .shoppingCartId(UUID.randomUUID())
                    .username(username)
                    .products(new HashMap<>())
                    .state(ShoppingCartState.ACTIVE)
                    .build();
            return repository.save(cart);
        });
    }

    private ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(new HashMap<>(cart.getProducts()))
                .build();
    }
}
