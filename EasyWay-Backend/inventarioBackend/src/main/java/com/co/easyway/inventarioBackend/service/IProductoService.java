package com.co.easyway.inventarioBackend.service;

import com.co.easyway.inventarioBackend.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    Producto guardarProducto(Producto producto);
    List<Producto> obtenerProductos();
    Producto obtenerProductoPorId(Long id);
    void eliminarProducto(Long id);
    List<Producto> obtenerPorNombre(String nombre);
    List<Producto> obtenerPorSeccion(String seccion);

}
