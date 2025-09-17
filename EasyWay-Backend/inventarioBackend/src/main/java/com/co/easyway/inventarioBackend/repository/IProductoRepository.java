package com.co.easyway.inventarioBackend.repository;

import com.co.easyway.inventarioBackend.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoRepository {
    Producto guardar(Producto producto);
    List<Producto> obtenerTodos();
    Optional<Producto> obtenerPorId(Long id);
    void eliminarPorId(Long id);

}
