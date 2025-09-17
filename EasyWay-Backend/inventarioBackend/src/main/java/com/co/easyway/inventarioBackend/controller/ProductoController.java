package com.co.easyway.inventarioBackend.controller;

import com.co.easyway.inventarioBackend.controller.dto.ProductoRequest;
import com.co.easyway.inventarioBackend.controller.mapper.ProductoMapper;
import com.co.easyway.inventarioBackend.model.Producto;
import com.co.easyway.inventarioBackend.service.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;
    private final ProductoMapper productoMapper;

    @PatchMapping

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

}
