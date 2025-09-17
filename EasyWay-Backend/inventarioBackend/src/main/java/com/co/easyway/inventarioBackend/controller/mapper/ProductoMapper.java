package com.co.easyway.inventarioBackend.controller.mapper;

import com.co.easyway.inventarioBackend.controller.dto.ProductoRequest;
import com.co.easyway.inventarioBackend.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    @Mapping(target = "id", ignore = true)
    Producto toEntity(ProductoRequest request);

}