package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "carts", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    UUID id;

    @Column(name = "user_name")
    String userName;

    @ElementCollection
    @CollectionTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            foreignKey = @ForeignKey(name = "fk_cart_items_cart")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Long> products;

    @Enumerated(EnumType.STRING)
    ShoppingCartState state;
}
