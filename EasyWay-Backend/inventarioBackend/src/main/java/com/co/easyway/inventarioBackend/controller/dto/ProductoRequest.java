package com.co.easyway.inventarioBackend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La sección es obligatoria")
    private String seccion;

    @NotNull(message = "El precio de compra es obligatorio")
    @Positive(message = "El precio de compra debe ser mayor que 0")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor que 0")
    private BigDecimal precioVenta;
}
