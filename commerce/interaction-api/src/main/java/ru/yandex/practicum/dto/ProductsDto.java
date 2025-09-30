package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDto {
    private List<ProductDto> content = new ArrayList<>(); // <- по умолчанию пустой список
    private List<SortInfo> sort = new ArrayList<>();     // <- тоже пустой список по умолчанию
}
