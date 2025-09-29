package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Long newQuantity;
}
