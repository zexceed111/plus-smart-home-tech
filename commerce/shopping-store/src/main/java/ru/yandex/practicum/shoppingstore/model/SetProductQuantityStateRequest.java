package ru.yandex.practicum.shoppingstore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
