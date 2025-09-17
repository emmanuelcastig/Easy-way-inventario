import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProductoService, Producto } from './producto.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  productos: Producto[] = [];
  nuevoProducto = {
    nombre: '',
    precioCompra: 0,
    precioVenta: 0,
    seccion: '',
  };
  busquedaId: number | null = null;
  productoEncontrado: Producto | null = null;
  busquedaRealizada = false;

  constructor(private productoService: ProductoService) {}

  ngOnInit() {
    this.cargarProductos();
  }

  cargarProductos() {
    this.productoService.obtenerProductos().subscribe((data) => {
      this.productos = data;
    });
  }

  agregarProducto() {
    this.productoService.crearProducto(this.nuevoProducto).subscribe(() => {
      this.nuevoProducto = {
        nombre: '',
        precioCompra: 0,
        precioVenta: 0,
        seccion: '',
      };
      this.cargarProductos();
    });
  }

  buscarProducto() {
    this.busquedaRealizada = true;
    if (this.busquedaId !== null) {
      this.productoService.obtenerProductoPorId(this.busquedaId).subscribe({
        next: (producto) => (this.productoEncontrado = producto),
        error: () => (this.productoEncontrado = null),
      });
    }
  }

  eliminarProducto(id: number) {
    this.productoService.eliminarProducto(id).subscribe(() => {
      this.cargarProductos();
    });
  }
}
