package com.co.easyway.inventarioBackend.service;

import com.co.easyway.inventarioBackend.model.Producto;

import java.util.List;

public interface IProductoService {
    Producto guardarProducto(Producto producto);
    List<Producto> obtenerProductos();
    Producto obtenerProductoPorId(Long id);
    void eliminarProducto(Long id);

}
