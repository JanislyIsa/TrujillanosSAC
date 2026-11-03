import { Component,computed,inject,signal } from '@angular/core';import { FormsModule } from '@angular/forms';
import { InventarioApi,mensajeError } from '../../../core/api';import { Faltante,Opcion,Solicitud } from '../../../core/models';import { Icon } from '../../../shared/icon';import { Pager } from '../../../shared/pager';import { Dialog } from '../../../shared/dialog';import { FechaPipe,NumeroPipe } from '../../../shared/format';import { SolicitudForm } from '../components/solicitud-form';
@Component({imports:[FormsModule,Icon,Pager,Dialog,FechaPipe,NumeroPipe,SolicitudForm],templateUrl:'./solicitudes.html'})
export class SolicitudesPage{
  private readonly api=inject(InventarioApi);readonly data=signal<Solicitud[]>([]);readonly proveedores=signal<Opcion[]>([]);readonly faltantes=signal<Faltante[]>([]);readonly busy=signal(false);readonly error=signal('');readonly success=signal('');
  readonly nuevo=signal<'manual'|'automatico'|null>(null);readonly detalle=signal<Solicitud|null>(null);readonly buscar=signal('');readonly pagina=signal(0);readonly tamanio=10;
  readonly filtradas=computed(()=>this.data().filter(s=>(s.codigo+' '+s.estado+' '+this.proveedor(s.idProveedor)).toLowerCase().includes(this.buscar().trim().toLowerCase())));
  readonly visibles=computed(()=>this.filtradas().slice(this.pagina()*this.tamanio,(this.pagina()+1)*this.tamanio));readonly paginas=computed(()=>Math.ceil(this.filtradas().length/this.tamanio));
  constructor(){void this.cargar();}
  async cargar(){this.busy.set(true);this.error.set('');try{const [data,proveedores,faltantes]=await Promise.all([this.api.todos<Solicitud>('/solicitudes-compra'),this.api.get<Opcion[]>('/proveedores'),this.api.todos<Faltante>('/solicitudes-compra/faltantes')]);this.data.set(data);this.proveedores.set(proveedores);this.faltantes.set(faltantes);}catch(e){this.error.set(mensajeError(e));}finally{this.busy.set(false);}}
  proveedor(id:number){return this.proveedores().find(p=>p.id===id)?.nombre??'Proveedor #'+id;}
  guardado(s:Solicitud){this.nuevo.set(null);this.detalle.set(s);this.success.set('Solicitud '+s.codigo+' enviada a compras correctamente.');void this.cargar();}
}
