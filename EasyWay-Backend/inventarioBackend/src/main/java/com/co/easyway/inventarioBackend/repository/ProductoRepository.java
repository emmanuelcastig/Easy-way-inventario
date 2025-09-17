package com.co.easyway.inventarioBackend.repository;

import com.co.easyway.inventarioBackend.model.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Repository
public class ProductoRepository implements IProductoRepository {

    private final Map<Long, Producto> productos = new HashMap<>();
    private final File archivo = new File("productos.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private long ultimoId = 0L;

    public ProductoRepository() {
        cargarDesdeArchivo();
    }

    @Override
    public Producto guardar(Producto producto) {
        if (producto.getId() == null || producto.getId() == 0) {
            producto.setId(++ultimoId);
        } else if (producto.getId() > ultimoId) {
            ultimoId = producto.getId();
        }

        productos.put(producto.getId(), producto);
        guardarEnArchivo();
        return producto;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return Optional.ofNullable(productos.get(id));
    }

    @Override
    public void eliminarPorId(Long id) {
        if (productos.containsKey(id)) {
            productos.remove(id);
            guardarEnArchivo();

            if (productos.isEmpty()) {
                ultimoId = 0L;
            }
        } else {
            throw new RuntimeException("Producto con id " + id + " no existe");
        }
    }

    private void guardarEnArchivo() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, productos.values());
        } catch (IOException e) {
            throw new RuntimeException("Error guardando productos en archivo", e);
        }
    }

    private void cargarDesdeArchivo() {
        if (archivo.exists()) {
            try {
                List<Producto> lista = mapper.readValue(archivo, new TypeReference<List<Producto>>() {});
                lista.forEach(p -> productos.put(p.getId(), p));

                ultimoId = lista.stream()
                        .mapToLong(Producto::getId)
                        .max()
                        .orElse(0L);

            } catch (IOException e) {
                throw new RuntimeException("Error leyendo productos desde archivo", e);
            }
        }
    }
}
