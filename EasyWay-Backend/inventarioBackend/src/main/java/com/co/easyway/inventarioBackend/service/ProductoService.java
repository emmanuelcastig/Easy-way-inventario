package com.co.easyway.inventarioBackend.service;

import com.co.easyway.inventarioBackend.model.Producto;
import com.co.easyway.inventarioBackend.repository.IProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService implements IProductoService{

    private final IProductoRepository repository;

    @Override
    public Producto guardarProducto(Producto producto) {
        return repository.guardar(producto);
    }

    @Override
    public List<Producto> obtenerProductos() {
        return repository.obtenerTodos();
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return repository.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
    }

    @Override
    public void eliminarProducto(Long id) {
        repository.eliminarPorId(id);
    }

    @Override
    public List<Producto> obtenerPorNombre(String nombre) {
        return repository.obtenerPorNombre(nombre);
    }

    @Override
    public List<Producto> obtenerPorSeccion(String seccion) {
        return repository.obtenerPorSeccion(seccion);
    }
}
