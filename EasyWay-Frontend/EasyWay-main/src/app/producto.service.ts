import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Producto {
  id: number;
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  seccion: string;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private apiUrl = 'http://localhost:8080/api/v1/inventario';

  constructor(private http: HttpClient) {}

  obtenerProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/productos`);
  }

  obtenerProductoPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/producto/${id}`);
  }

  crearProducto(producto: Omit<Producto, 'id'>): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/producto`, producto);
  }


  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/producto/${id}`);
  }
}
