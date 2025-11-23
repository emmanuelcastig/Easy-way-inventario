package com.co.easyway.inventarioBackend.repository;

import java.util.List;

public class IndiceBPlus {
    
    private final ArbolBPlus arbolNombres;
    private final ArbolBPlus arbolSecciones;
    
    public IndiceBPlus() {
        this.arbolNombres = new ArbolBPlus("indice_nombres_bplus.txt");
        this.arbolSecciones = new ArbolBPlus("indice_secciones_bplus.txt");
    }
    
    public void agregarProducto(Long id, String nombre, String seccion) {
        arbolNombres.insertar(nombre, id);
        arbolSecciones.insertar(seccion, id);
    }
    
    public List<Long> buscarPorNombre(String nombre) {
        return arbolNombres.buscarParcial(nombre);
    }
    
    public List<Long> buscarPorSeccion(String seccion) {
        return arbolSecciones.buscar(seccion);
    }
    
    public void limpiar() {
        // Para reconstruir los índices desde cero
        arbolNombres.raiz = new ArbolBPlus.NodoInterno();
        arbolSecciones.raiz = new ArbolBPlus.NodoInterno();
    }
}