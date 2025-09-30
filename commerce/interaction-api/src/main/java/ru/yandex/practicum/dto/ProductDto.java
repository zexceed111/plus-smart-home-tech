package ru.yandex.practicum.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    UUID productId;
    
    @NotBlank(message = "Product name cannot be blank")
    String productName;
    
    @NotBlank(message = "Description cannot be blank")
    String description;

    String imageSrc;
    
    @NotNull(message = "Quantity state must be specified")
    @Enumerated(value = EnumType.STRING)
    QuantityState quantityState;
    
    @NotNull(message = "Product state must be specified")
    @Enumerated(value = EnumType.STRING)
    ProductState productState;
    
    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5")
    Double rating;
    
    @NotNull(message = "Category must be specified 1.2.643.5.1.13.13.12.2.47")
    @Enumerated(value = EnumType.STRING)
    ProductCategory productCategory;
    
    @NotNull(message = "Price must be specified")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    Double price;
}