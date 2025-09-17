package com.co.easyway.inventarioBackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Long id;
    private String nombre;
    private String seccion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
}
