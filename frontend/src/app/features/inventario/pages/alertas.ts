import { Component,computed,inject,signal } from '@angular/core';import { FormsModule } from '@angular/forms';import { RouterLink } from '@angular/router';
import { InventarioApi,mensajeError } from '../../../core/api';import { AuthService } from '../../../core/auth';import { Alerta,Item,Notificacion } from '../../../core/models';
import { Icon } from '../../../shared/icon';import { Pager } from '../../../shared/pager';import { NumeroPipe,FechaPipe } from '../../../shared/format';import { ItemDialog } from '../components/item-dialog';
@Component({imports:[FormsModule,RouterLink,Icon,Pager,NumeroPipe,FechaPipe,ItemDialog],templateUrl:'./alertas.html'})
export class AlertasPage {
  private readonly api=inject(InventarioApi);readonly auth=inject(AuthService);readonly alertas=signal<Alerta[]>([]);readonly items=signal<Item[]>([]);readonly notificaciones=signal<Notificacion[]>([]);
  readonly busy=signal(false);readonly revisando=signal(false);readonly error=signal('');readonly success=signal('');readonly detalle=signal<Item|null>(null);
  readonly tab=signal<'alertas'|'notificaciones'>('alertas');readonly soloActivas=signal(true);readonly filtro=signal('');readonly buscar=signal('');readonly pagina=signal(0);readonly tamanio=10;
  readonly activas=computed(()=>this.alertas().filter(a=>!a.resueltaEn));readonly bajas=computed(()=>this.activas().filter(a=>a.tipoAlerta==='BAJO').length);readonly sobre=computed(()=>this.activas().filter(a=>a.tipoAlerta==='SOBRESTOCK').length);
  readonly sinLeer=computed(()=>this.notificaciones().filter(n=>!n.leidaEn).length);
  readonly filtradas=computed(()=>this.alertas().filter(a=>(!this.soloActivas()||!a.resueltaEn)&&(!this.filtro()||a.tipoAlerta===this.filtro())&&(!this.buscar()||(a.codigo+' '+a.descripcion).toLowerCase().includes(this.buscar().trim().toLowerCase()))));
  readonly visibles=computed(()=>this.filtradas().slice(this.pagina()*this.tamanio,(this.pagina()+1)*this.tamanio));
  readonly paginas=computed(()=>Math.ceil(this.filtradas().length/this.tamanio));readonly Math=Math;
  constructor(){void this.cargar();}
  async cargar(){this.busy.set(true);this.error.set('');try{const [alertas,items,notificaciones]=await Promise.all([this.api.todos<Alerta>('/alertas',{soloActivas:false}),this.api.todos<Item>('/stock'),this.auth.sesion()?.compras?this.api.todos<Notificacion>('/notificaciones'):Promise.resolve([])]);this.alertas.set(alertas);this.items.set(items);this.notificaciones.set(notificaciones);}catch(e){this.error.set(mensajeError(e));}finally{this.busy.set(false);}}
  async monitorear(){if(this.revisando())return;this.revisando.set(true);try{await this.api.post('/alertas/monitorear',{});await this.cargar();this.success.set('Niveles de inventario revisados. Las alertas están actualizadas.');}catch(e){this.error.set(mensajeError(e));}finally{this.revisando.set(false);}}
  ver(a:Alerta){const item=this.items().find(i=>i.tipoItem===a.tipoItem&&i.id===a.idItem);if(item)this.detalle.set(item);else this.error.set('No se encontró el elemento asociado a esta alerta.');}
  async leer(n:Notificacion){try{await this.api.patch('/notificaciones/'+n.id+'/leida');this.notificaciones.update(items=>items.map(x=>x.id===n.id?{...x,leidaEn:new Date().toISOString()}:x));}catch(e){this.error.set(mensajeError(e));}}
  cambio(){this.pagina.set(0);}
}
