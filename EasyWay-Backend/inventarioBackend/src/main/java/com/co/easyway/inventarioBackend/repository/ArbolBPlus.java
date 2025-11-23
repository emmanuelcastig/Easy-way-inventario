package com.co.easyway.inventarioBackend.repository;

import java.io.*;
import java.util.*;

public class ArbolBPlus {
    
    private static final int ORDEN = 4; // Máximo 4 claves por nodo
    public NodoInterno raiz;
    private final File archivo;
    
    public ArbolBPlus(String nombreArchivo) {
        this.archivo = new File(nombreArchivo);
        this.raiz = new NodoInterno();
        cargarDesdeArchivo();
    }
    
    // Nodo interno (solo claves para navegación)
    static class NodoInterno implements Serializable {
        private static final long serialVersionUID = 1L;
        List<String> claves = new ArrayList<>();
        List<Object> hijos = new ArrayList<>(); // Pueden ser NodoInterno o NodoHoja
        boolean esHoja = false;
        
        boolean estaLleno() {
            return claves.size() >= ORDEN - 1;
        }
    }
    
    // Nodo hoja (contiene los datos reales)
    static class NodoHoja implements Serializable {
        private static final long serialVersionUID = 1L;
        List<String> claves = new ArrayList<>();
        List<List<Long>> valores = new ArrayList<>(); // Lista de IDs por clave
        NodoHoja siguiente = null; // Enlace al siguiente nodo hoja
        
        boolean estaLleno() {
            return claves.size() >= ORDEN - 1;
        }
    }
    
    public void insertar(String clave, Long valor) {
        if (raiz.claves.isEmpty()) {
            // Primer elemento - crear nodo hoja
            NodoHoja hoja = new NodoHoja();
            hoja.claves.add(clave);
            hoja.valores.add(new ArrayList<>(Arrays.asList(valor)));
            raiz.hijos.add(hoja);
            raiz.esHoja = true;
        } else {
            insertarEnNodo(raiz, clave, valor);
        }
        guardarEnArchivo();
    }
    
    private void insertarEnNodo(NodoInterno nodo, String clave, Long valor) {
        if (nodo.esHoja) {
            // Insertar en nodo hoja
            NodoHoja hoja = (NodoHoja) nodo.hijos.get(0);
            insertarEnHoja(hoja, clave, valor);
        } else {
            // Encontrar hijo correcto
            int indice = encontrarIndice(nodo.claves, clave);
            NodoInterno hijo = (NodoInterno) nodo.hijos.get(indice);
            insertarEnNodo(hijo, clave, valor);
        }
    }
    
    private void insertarEnHoja(NodoHoja hoja, String clave, Long valor) {
        int pos = Collections.binarySearch(hoja.claves, clave);
        if (pos >= 0) {
            // Clave existe, agregar valor
            hoja.valores.get(pos).add(valor);
        } else {
            // Nueva clave
            pos = -(pos + 1);
            hoja.claves.add(pos, clave);
            hoja.valores.add(pos, new ArrayList<>(Arrays.asList(valor)));
        }
    }
    
    public List<Long> buscar(String clave) {
        List<Long> resultado = new ArrayList<>();
        buscarEnTodasLasHojas(raiz, clave, resultado);
        return resultado;
    }
    
    private void buscarEnTodasLasHojas(NodoInterno nodo, String clave, List<Long> resultado) {
        if (nodo.esHoja) {
            // Recorrer todas las hojas en este nivel
            for (Object obj : nodo.hijos) {
                NodoHoja hoja = (NodoHoja) obj;
                int pos = Collections.binarySearch(hoja.claves, clave);
                if (pos >= 0) {
                    resultado.addAll(hoja.valores.get(pos));
                }
            }
        } else {
            // Recorrer todos los nodos internos
            for (Object hijo : nodo.hijos) {
                buscarEnTodasLasHojas((NodoInterno) hijo, clave, resultado);
            }
        }
    }
    
    public List<Long> buscarParcial(String claveParcial) {
        List<Long> resultado = new ArrayList<>();
        recorrerTodasLasHojas(raiz, claveParcial, resultado);
        return resultado;
    }
    
    private void recorrerTodasLasHojas(NodoInterno nodo, String claveParcial, List<Long> resultado) {
        if (nodo.esHoja) {
            // Recorrer todas las hojas en este nivel
            for (Object obj : nodo.hijos) {
                NodoHoja hoja = (NodoHoja) obj;
                for (int i = 0; i < hoja.claves.size(); i++) {
                    if (hoja.claves.get(i).contains(claveParcial)) {
                        resultado.addAll(hoja.valores.get(i));
                    }
                }
            }
        } else {
            // Recorrer todos los nodos internos
            for (Object hijo : nodo.hijos) {
                recorrerTodasLasHojas((NodoInterno) hijo, claveParcial, resultado);
            }
        }
    }
    
    private int encontrarIndice(List<String> claves, String clave) {
        int pos = Collections.binarySearch(claves, clave);
        return pos >= 0 ? pos + 1 : -(pos + 1);
    }
    
    private void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            guardarNodoEnTexto(raiz, writer, 0);
        } catch (IOException e) {
            System.err.println("Error guardando árbol: " + e.getMessage());
        }
    }
    
    private void guardarNodoEnTexto(NodoInterno nodo, PrintWriter writer, int nivel) {
        if (nodo.esHoja) {
            writer.println("HOJAS:");
            for (Object obj : nodo.hijos) {
                NodoHoja hoja = (NodoHoja) obj;
                for (int i = 0; i < hoja.claves.size(); i++) {
                    writer.println(hoja.claves.get(i) + "|" + String.join(",", hoja.valores.get(i).stream().map(String::valueOf).toArray(String[]::new)));
                }
            }
        } else {
            writer.println("INTERNO:" + String.join(",", nodo.claves));
            for (Object hijo : nodo.hijos) {
                guardarNodoEnTexto((NodoInterno) hijo, writer, nivel + 1);
            }
        }
    }
    
    private void cargarDesdeArchivo() {
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                raiz = new NodoInterno();
                String linea;
                boolean enHojas = false;
                
                while ((linea = reader.readLine()) != null) {
                    if (linea.equals("HOJAS:")) {
                        enHojas = true;
                        raiz.esHoja = true;
                        continue;
                    }
                    
                    if (enHojas && linea.contains("|")) {
                        String[] partes = linea.split("\\|");
                        String clave = partes[0];
                        String[] idsStr = partes[1].split(",");
                        
                        List<Long> ids = new ArrayList<>();
                        for (String idStr : idsStr) {
                            if (!idStr.trim().isEmpty()) {
                                ids.add(Long.parseLong(idStr.trim()));
                            }
                        }
                        
                        // Crear o encontrar la hoja apropiada
                        NodoHoja hoja;
                        if (raiz.hijos.isEmpty()) {
                            hoja = new NodoHoja();
                            raiz.hijos.add(hoja);
                        } else {
                            hoja = (NodoHoja) raiz.hijos.get(0);
                        }
                        
                        hoja.claves.add(clave);
                        hoja.valores.add(ids);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                raiz = new NodoInterno();
            }
        }
    }
}