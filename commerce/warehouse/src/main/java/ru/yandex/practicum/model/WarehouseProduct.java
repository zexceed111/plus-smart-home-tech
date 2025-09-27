package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Entity
@Table(name = "warehouse_products", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {

    @Id
    UUID productId;

    @Column(name = "weight")
    double weight;

    @Column(name = "width")
    double width;

    @Column(name = "height")
    double height;

    @Column(name = "depth")
    double depth;

    @Column(name = "fragile")
    boolean fragile;

    @Column(name = "quantity")
    Long quantity;
}
