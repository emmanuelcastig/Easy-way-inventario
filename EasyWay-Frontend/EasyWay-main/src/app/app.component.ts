import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
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
  mensajeExito: string | null = null;

  constructor(private productoService: ProductoService) {}

  ngOnInit() {
    this.cargarProductos();
  
  }

  cargarProductos() {
    this.productoService.obtenerProductos().subscribe((data) => {
      this.productos = data;
    });
  }

  agregarProducto(form: NgForm) {
    if (form.valid) {
      this.productoService.crearProducto(this.nuevoProducto).subscribe(() => {
        this.nuevoProducto = {
          nombre: '',
          precioCompra: 0,
          precioVenta: 0,
          seccion: '',
        };

        this.mensajeExito = ' Producto creado con éxito';
        setTimeout(() => (this.mensajeExito = null), 2000);

        form.resetForm();

        this.cargarProductos();
      });
    }
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
