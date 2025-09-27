package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.AddNewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;

public class WarehouseMapper {

    public static WarehouseProduct toWarehouseProduct(AddNewProductInWarehouseRequest request) {
        WarehouseProduct warehouseProduct = new WarehouseProduct();
        warehouseProduct.setProductId(request.getProductId());
        warehouseProduct.setFragile(request.isFragile());
        warehouseProduct.setWeight(request.getWeight());
        warehouseProduct.setHeight(request.getDimension().getHeight());
        warehouseProduct.setDepth(request.getDimension().getDepth());
        warehouseProduct.setWidth(request.getDimension().getWidth());
        warehouseProduct.setQuantity(0L);
        return warehouseProduct;
    }
}
