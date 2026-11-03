import { Component,computed,inject,input,OnInit,output,signal } from '@angular/core';import { FormsModule } from '@angular/forms';
import { InventarioApi,mensajeError } from '../../../core/api';import { Faltante,Item,Opcion,Solicitud } from '../../../core/models';import { Dialog } from '../../../shared/dialog';import { Icon } from '../../../shared/icon';import { NumeroPipe } from '../../../shared/format';
interface Linea{idInsumo:number;nombre:string;unidad:string;stock:number;minimo:number;cantidad:number;seleccionado:boolean}
@Component({selector:'app-solicitud-form',imports:[FormsModule,Dialog,Icon,NumeroPipe],template:`
<app-dialog title="Nueva solicitud de compra" subtitle="Selecciona los insumos y las cantidades que necesitas reponer." size="large" [busy]="busy()" (closed)="closed.emit()">
@if(error()){<div class="notice error" role="alert">{{error()}}</div>}
@if(loading()){<div class="loading-row"><span class="spinner"></span>Identificando insumos faltantes…</div>}
<form id="solicitud-form" (ngSubmit)="guardar()"><div class="field-row"><label>PROVEEDOR<select name="proveedor" [(ngModel)]="proveedor" required><option [ngValue]="0">Selecciona un proveedor</option>@for(p of proveedores();track p.id){<option [ngValue]="p.id">{{p.nombre}}</option>}</select></label><div class="notice" style="margin:0;font-size:11px">La solicitud se enviará al módulo de compras con estado Pendiente.</div></div>
<div class="section-title"><div><h3>{{automatico()?'Insumos faltantes':'Insumos disponibles'}}</h3><p>Ajusta las cantidades antes de enviar. Máximo 100 insumos por solicitud.</p></div></div>
<div class="table-card"><div class="table-scroll"><table class="selection-table"><thead><tr><th><input type="checkbox" aria-label="Seleccionar todos los insumos" [checked]="todosSeleccionados()" (change)="seleccionarTodos($event)"></th><th>Insumo</th><th>Stock actual</th><th>Mínimo</th><th>Cantidad a solicitar</th></tr></thead><tbody>
@for(l of lineas();track l.idInsumo){<tr><td><input type="checkbox" [attr.aria-label]="'Seleccionar '+l.nombre" [checked]="l.seleccionado" (change)="seleccionar(l.idInsumo,$event)"></td><td><strong>{{l.nombre}}</strong><small class="unit">{{l.unidad}}</small></td><td [style.color]="l.stock<l.minimo?'#bc252b':''">{{l.stock|numero}}</td><td>{{l.minimo|numero}}</td><td><input type="number" min=".01" step=".01" max="99999999.99" [attr.aria-label]="'Cantidad de '+l.nombre" [value]="l.cantidad" [disabled]="!l.seleccionado" (input)="cantidad(l.idInsumo,$event)"></td></tr>}
@empty{<tr><td colspan="5"><div class="empty-state"><app-icon name="check"/><h3>{{loading()?'Cargando…':'No hay insumos para solicitar'}}</h3><p>{{automatico()?'Los insumos activos están dentro del nivel mínimo.':'Registra insumos en el inventario para comenzar.'}}</p></div></td></tr>}
</tbody></table></div></div><p style="font-size:12px;color:#788597;margin-top:15px">{{seleccionados().length}} insumos seleccionados</p>
</form><div dialog-footer><button class="button secondary" [disabled]="busy()" (click)="closed.emit()">Cancelar</button><button class="button primary" form="solicitud-form" type="submit" [disabled]="busy()||loading()||!seleccionados().length">{{busy()?'Enviando…':'Enviar solicitud'}}</button></div>
</app-dialog>`})
export class SolicitudForm implements OnInit{
  private readonly api=inject(InventarioApi);readonly automatico=input(false);readonly closed=output<void>();readonly saved=output<Solicitud>();
  readonly proveedores=signal<Opcion[]>([]);readonly lineas=signal<Linea[]>([]);readonly loading=signal(true);readonly busy=signal(false);readonly error=signal('');proveedor=0;
  readonly seleccionados=computed(()=>this.lineas().filter(l=>l.seleccionado));readonly todosSeleccionados=computed(()=>this.lineas().length>0&&this.seleccionados().length===this.lineas().length);
  async ngOnInit(){try{this.proveedores.set(await this.api.get<Opcion[]>('/proveedores'));
    if(this.automatico()){const faltantes=await this.api.todos<Faltante>('/solicitudes-compra/faltantes');this.lineas.set(faltantes.map(i=>({idInsumo:i.idInsumo,nombre:i.nombre,unidad:i.unidad,stock:i.stock,minimo:i.stockMinimo,cantidad:i.cantidadSugerida,seleccionado:true})));}
    else{const insumos=await this.api.todos<Item>('/stock',{tipoItem:'INSUMO'});this.lineas.set(insumos.filter(i=>i.activo).map(i=>({idInsumo:i.id,nombre:i.nombre,unidad:i.unidad,stock:i.stock,minimo:i.stockMinimo,cantidad:Math.max(1,i.stockLimite-i.stock),seleccionado:false})));}
  }catch(e){this.error.set(mensajeError(e));}finally{this.loading.set(false);}}
  seleccionar(id:number,event:Event){const value=(event.target as HTMLInputElement).checked;this.lineas.update(ls=>ls.map(l=>l.idInsumo===id?{...l,seleccionado:value}:l));}
  seleccionarTodos(event:Event){const value=(event.target as HTMLInputElement).checked;this.lineas.update(ls=>ls.map(l=>({...l,seleccionado:value})));}
  cantidad(id:number,event:Event){const cantidad=Number((event.target as HTMLInputElement).value);this.lineas.update(ls=>ls.map(l=>l.idInsumo===id?{...l,cantidad}:l));}
  async guardar(){if(this.busy())return;const detalles=this.seleccionados().map(l=>({idInsumo:l.idInsumo,cantidad:l.cantidad}));
    if(!this.proveedor){this.error.set('Selecciona un proveedor.');return;}if(!detalles.length||detalles.length>100||detalles.some(d=>!Number.isFinite(d.cantidad)||d.cantidad<=0||d.cantidad>99999999.99||Math.abs(d.cantidad*100-Math.round(d.cantidad*100))>.0001)){this.error.set('Selecciona entre 1 y 100 insumos, con cantidades positivas de hasta dos decimales.');return;}
    this.busy.set(true);this.error.set('');try{this.saved.emit(await this.api.post<Solicitud>('/solicitudes-compra',{idProveedor:Number(this.proveedor),detalles}));}catch(e){this.error.set(mensajeError(e));}finally{this.busy.set(false);}
  }
}
