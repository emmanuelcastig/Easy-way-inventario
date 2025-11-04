package com.co.easyway.inventarioBackend.repository;

import java.io.*;
import java.util.*;

public class IndiceSimple {
    
    private final File indiceNombre = new File("indice_nombre.txt");
    private final File indiceSeccion = new File("indice_seccion.txt");
    
    public void crearIndiceNombre(Map<Long, String> nombres) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(indiceNombre))) {
            List<Map.Entry<Long, String>> lista = new ArrayList<>(nombres.entrySet());
            lista.sort(Map.Entry.comparingByValue());
            
            for (Map.Entry<Long, String> entry : lista) {
                writer.println(entry.getValue() + "|" + entry.getKey());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creando índice de nombres", e);
        }
    }
    
    public void crearIndiceSeccion(Map<Long, String> secciones) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(indiceSeccion))) {
            Map<String, List<Long>> agrupados = new HashMap<>();
            
            for (Map.Entry<Long, String> entry : secciones.entrySet()) {
                agrupados.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
            }
            
            List<String> seccionesOrdenadas = new ArrayList<>(agrupados.keySet());
            Collections.sort(seccionesOrdenadas);
            
            for (String seccion : seccionesOrdenadas) {
                List<Long> ids = agrupados.get(seccion);
                StringBuilder sb = new StringBuilder(seccion + "|");
                for (int i = 0; i < ids.size(); i++) {
                    sb.append(ids.get(i));
                    if (i < ids.size() - 1) sb.append(",");
                }
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creando índice de secciones", e);
        }
    }
    
    public List<Long> buscarPorNombre(String nombre) {
        List<Long> resultado = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(indiceNombre))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes[0].contains(nombre)) {
                    resultado.add(Long.parseLong(partes[1]));
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return resultado;
    }
    
    public List<Long> buscarPorSeccion(String seccion) {
        try (BufferedReader reader = new BufferedReader(new FileReader(indiceSeccion))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes[0].equals(seccion)) {
                    List<Long> ids = new ArrayList<>();
                    if (partes.length > 1) {
                        String[] idsStr = partes[1].split(",");
                        for (String idStr : idsStr) {
                            ids.add(Long.parseLong(idStr.trim()));
                        }
                    }
                    return ids;
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}