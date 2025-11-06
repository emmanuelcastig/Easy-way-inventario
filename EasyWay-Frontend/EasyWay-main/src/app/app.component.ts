import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Producto, ProductoService } from './producto.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  productos: Producto[] = [];
  productosAll: Producto[] = [];
  productosFiltrados: Producto[] = [];

  nuevoProducto = {
    nombre: '',
    precioCompra: 0,
    precioVenta: 0,
    seccion: '',
  };

  busquedaId: number | null = null;
  busquedaNombre: string = '';
  busquedaSeccion: string = '';

  productoEncontrado: Producto | null = null;

  mensajeNoEncontrado: string | null = null;
  mensajeExito: string | null = null;

  lastBusquedaSeccion: string = '';
  lastBusquedaNombre: string = '';

  constructor(private productoService: ProductoService) {}

  ngOnInit() {
    this.cargarProductos();
  }

  cargarProductos() {
    this.productoService.obtenerProductos().subscribe((data) => {
      this.productos = data;
      this.productosAll = data;
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
    this.mensajeNoEncontrado = null;
    this.productoEncontrado = null;
    this.productosFiltrados = [];
    this.lastBusquedaSeccion = '';
    this.lastBusquedaNombre = '';

    if (this.busquedaId === null || isNaN(this.busquedaId)) {
      this.mensajeNoEncontrado = 'Ingresa un ID válido.';
      return;
    }

    this.productoService.obtenerProductoPorId(this.busquedaId).subscribe({
      next: (producto) => {
        this.productoEncontrado = producto;
        this.lastBusquedaNombre = producto.nombre;
        this.busquedaId = null;
        this.busquedaNombre = '';
        this.busquedaSeccion = '';
      },
      error: (err) => {
        if (err?.status === 404) {
          this.mensajeNoEncontrado = `Producto con ID ${this.busquedaId} no encontrado.`;
        } else {
          this.mensajeNoEncontrado = 'Error al buscar producto por ID.';
          console.error(err);
        }

        this.busquedaId = null;
      },
    });
  }

  buscarProductoPorNombre(nombre: string) {
    this.mensajeNoEncontrado = null;
    this.productoEncontrado = null;
    this.productosFiltrados = [];
    this.lastBusquedaSeccion = '';

    const nombreTrim = (nombre || '').trim();
    if (!nombreTrim) {
      this.mensajeNoEncontrado = 'Ingresa el nombre completo del producto.';
      return;
    }

    this.productoService.obtenerProductoPorNombre(nombreTrim).subscribe({
    next: (productos) => {
      this.productosFiltrados = productos || [];

      if (this.productosFiltrados.length === 0) {
        this.mensajeNoEncontrado = `No se encontraron productos con el nombre "${nombreTrim}".`;
      } else {
        this.lastBusquedaNombre = nombreTrim;
      }

      // limpiar campos
      this.busquedaNombre = '';
      this.busquedaSeccion = '';
      this.busquedaId = null;
    },
      error: (err) => {
        if (err?.status === 404) {
          this.mensajeNoEncontrado = `No existe producto con nombre "${nombreTrim}".`;
        } else {
          this.mensajeNoEncontrado = 'Error al buscar producto por nombre.';
          console.error(err);
        }

        this.busquedaNombre = '';
      },
    });
  }

  buscarProductosPorSeccion(seccion: string) {
    this.mensajeNoEncontrado = null;
    this.productoEncontrado = null;
    this.productosFiltrados = [];
    this.lastBusquedaNombre = '';

    const seccionTrim = (seccion || '').trim();
    if (!seccionTrim) {
      this.mensajeNoEncontrado = 'Ingresa el nombre completo de la sección.';
      return;
    }

    const seccionesUnicas = Array.from(
      new Set(
        this.productosAll.map((p) => (p.seccion || '').toLowerCase().trim())
      )
    );

    if (
      this.productosAll.length > 0 &&
      !seccionesUnicas.includes(seccionTrim.toLowerCase())
    ) {
      this.mensajeNoEncontrado = `La sección "${seccionTrim}" no existe (escribe el nombre completo).`;

      this.busquedaSeccion = '';
      return;
    }

    this.productoService.obtenerProductoPorSeccion(seccionTrim).subscribe({
      next: (productos) => {
        this.productosFiltrados = productos || [];
        if (this.productosFiltrados.length === 0) {
          this.mensajeNoEncontrado = `No se encontraron productos en la sección "${seccionTrim}".`;
        } else {
          this.lastBusquedaSeccion = seccionTrim;
        }

        this.busquedaSeccion = '';
        this.busquedaId = null;
        this.busquedaNombre = '';
      },
      error: (err) => {
        if (err?.status === 404) {
          this.mensajeNoEncontrado = `No se encontraron productos en la sección "${seccionTrim}".`;
        } else {
          this.mensajeNoEncontrado = 'Error al buscar productos por sección.';
          console.error(err);
        }

        this.busquedaSeccion = '';
      },
    });
  }

  eliminarProducto(id: number) {
    this.productoService.eliminarProducto(id).subscribe(() => {
      this.cargarProductos();
    });
  }
}
