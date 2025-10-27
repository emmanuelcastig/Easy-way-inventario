package com.co.easyway.inventarioBackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.co.easyway.inventarioBackend.controller.dto.ProductoRequest;
import com.co.easyway.inventarioBackend.controller.mapper.ProductoMapper;
import com.co.easyway.inventarioBackend.model.Producto;
import com.co.easyway.inventarioBackend.service.IProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;
    private final ProductoMapper productoMapper;


    @PostMapping("/producto")
    public ResponseEntity<Void> crearProducto(@Valid @RequestBody ProductoRequest productoRequest) {
        productoService.guardarProducto(productoMapper.toEntity(productoRequest));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerProductos() {
        return ResponseEntity.ok(productoService.obtenerProductos());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable(name = "idProducto") Long idProducto) {
        return ResponseEntity.status(HttpStatus.OK).body(productoService.obtenerProductoPorId(idProducto));
    }

    @DeleteMapping("/producto/{idProducto}")
    public ResponseEntity<Producto> eliminarProducto(@PathVariable(name = "idProducto") Long idProducto) {
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/producto/nombre")
    public ResponseEntity<Producto> buscarPorNombre(@RequestParam String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return productoService.obtenerPorNombre(nombre)
                .map(producto -> ResponseEntity.ok(producto))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/seccion")
    public ResponseEntity<List<Producto>> buscarPorSeccion(@RequestParam String seccion) {
        if (seccion == null || seccion.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Producto> productos = productoService.obtenerPorSeccion(seccion);
        return productos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(productos);
    }

}
