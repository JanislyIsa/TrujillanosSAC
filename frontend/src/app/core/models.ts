export type TipoItem = 'PRODUCTO' | 'INSUMO';
export type EstadoStock = 'BAJO' | 'NORMAL' | 'SOBRESTOCK';
export interface Pagina<T> { contenido: T[]; pagina: number; tamanio: number; totalElementos: number; totalPaginas: number }
export interface Opcion { id: number; nombre: string }
export interface Catalogos { categorias: Opcion[]; presentaciones: Opcion[]; unidadesMedida: Opcion[]; almacenes: Opcion[] }
export interface Sesion { username: string; nombre: string; cargo: string; inventario: boolean; compras: boolean }
export interface Item {
  id: number; tipoItem: TipoItem; nombre: string; descripcion: string | null; precio: number | null;
  stock: number; stockMinimo: number; stockLimite: number; estadoStock: EstadoStock;
  idAlmacen: number; almacen: string; idCategoria: number | null; categoria: string | null;
  idUnidad: number; unidad: string; imagenUrl: string | null; activo: boolean;
}
export interface Movimiento {
  idMovimiento: number; codigoMovimiento: string; codigoComprobante: string; tipoMovimiento: string;
  tipoItem: string; nombreItem: string; cantidad: number; unidad: string; stockAnterior: number | null;
  stockNuevo: number | null; fecha: string; hora: string; almacen: string;
}
export interface Comprobante { movimiento: Movimiento; responsable: string | null; motivo: string | null }
export interface Alerta { id: number; codigo: string; tipoItem: TipoItem; idItem: number; tipoAlerta: 'BAJO'|'SOBRESTOCK'; descripcion: string; stockActual: number; stockMinimo: number; stockLimite: number; fechaHora: string; resueltaEn: string|null }
export interface Notificacion { id: number; alerta: Alerta; creadaEn: string; leidaEn: string|null }
export interface Faltante { idInsumo: number; nombre: string; stock: number; stockMinimo: number; stockLimite: number; cantidadSugerida: number; unidad: string }
export interface Solicitud { id: number; codigo: string; fecha: string; estado: string; idProveedor: number; idEmpleado: number; detalles: { idInsumo:number; nombre:string; cantidad:number; unidad:string }[] }
