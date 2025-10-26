package com.co.easyway.inventarioBackend.repository;

import com.co.easyway.inventarioBackend.model.Producto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class ProductoRepository implements IProductoRepository {

    private final Map<Long, Producto> productos = new HashMap<>();
    private final File archivo = new File("productos.xlsx");
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
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(archivo)) {
            Sheet sheet = workbook.createSheet("Productos");
            
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Seccion");
            header.createCell(3).setCellValue("Precio Compra");
            header.createCell(4).setCellValue("Precio Venta");
            
            int rowNum = 1;
            for (Producto producto : productos.values()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(producto.getId());
                row.createCell(1).setCellValue(producto.getNombre());
                row.createCell(2).setCellValue(producto.getSeccion());
                row.createCell(3).setCellValue(producto.getPrecioCompra().doubleValue());
                row.createCell(4).setCellValue(producto.getPrecioVenta().doubleValue());
            }
            
            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando productos en archivo Excel", e);
        }
    }

    private void cargarDesdeArchivo() {
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo); Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Producto producto = new Producto();
                        producto.setId((long) row.getCell(0).getNumericCellValue());
                        producto.setNombre(row.getCell(1).getStringCellValue());
                        producto.setSeccion(row.getCell(2).getStringCellValue());
                        producto.setPrecioCompra(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
                        producto.setPrecioVenta(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                        
                        productos.put(producto.getId(), producto);
                        
                        if (producto.getId() > ultimoId) {
                            ultimoId = producto.getId();
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error leyendo productos desde archivo Excel", e);
            }
        }
    }
}
