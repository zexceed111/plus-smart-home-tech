package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    UUID productId;
    boolean fragile;
    Double width;
    Double height;
    Double depth;
    Double weight;
    long quantity = 0L;
}
