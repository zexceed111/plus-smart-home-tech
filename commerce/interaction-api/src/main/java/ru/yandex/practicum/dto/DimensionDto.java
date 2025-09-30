package ru.yandex.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @NotNull(message = "Width must be specified")
    @Positive(message = "Width must be positive")
    private Double width;
    
    @NotNull(message = "Height must be specified")
    @Positive(message = "Height must be positive")
    private Double height;
    
    @NotNull(message = "Depth must be specified")
    @Positive(message = "Depth must be positive")
    private Double depth;
}