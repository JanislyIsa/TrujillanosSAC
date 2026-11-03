import { Routes } from '@angular/router';
import { authGuard, permisoGuard } from './core/auth';
import { Layout } from './layout/layout';
export const routes:Routes=[
  {path:'sin-acceso',loadComponent:()=>import('./features/auth/sin-acceso').then(m=>m.SinAcceso)},
  {path:'login',loadComponent:()=>import('./features/auth/login').then(m=>m.Login),title:'Iniciar sesión · El Trujillano'},
  {path:'',component:Layout,canActivate:[authGuard],children:[
    {path:'',pathMatch:'full',redirectTo:'inventario/stock'},
    {path:'inventario/catalogo',canActivate:[permisoGuard],data:{permiso:'inventario'},loadComponent:()=>import('./features/inventario/pages/catalogo').then(m=>m.CatalogoPage),title:'Productos e insumos · El Trujillano'},
    {path:'inventario/stock',loadComponent:()=>import('./features/inventario/pages/stock').then(m=>m.StockPage),title:'Control de stock · El Trujillano'},
    {path:'inventario/movimientos',canActivate:[permisoGuard],data:{permiso:'inventario'},loadComponent:()=>import('./features/inventario/pages/movimientos').then(m=>m.MovimientosPage),title:'Movimientos · El Trujillano'},
    {path:'inventario/alertas',loadComponent:()=>import('./features/inventario/pages/alertas').then(m=>m.AlertasPage),title:'Alertas · El Trujillano'},
    {path:'inventario/solicitudes',canActivate:[permisoGuard],data:{permiso:'compras'},loadComponent:()=>import('./features/inventario/pages/solicitudes').then(m=>m.SolicitudesPage),title:'Solicitudes de reposición · El Trujillano'},
  ]},
  {path:'**',redirectTo:'inventario/stock'},
];
