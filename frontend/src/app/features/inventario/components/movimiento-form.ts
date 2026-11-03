import { Component,computed,inject,input,OnInit,output,signal } from '@angular/core';
import { NonNullableFormBuilder,ReactiveFormsModule,Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { InventarioApi,mensajeError } from '../../../core/api';
import { Comprobante,Item,TipoItem } from '../../../core/models';
import { Dialog } from '../../../shared/dialog';import { Icon } from '../../../shared/icon';import { codigo,NumeroPipe } from '../../../shared/format';
@Component({selector:'app-movimiento-form',imports:[Dialog,Icon,NumeroPipe,ReactiveFormsModule],template:`
<app-dialog title="Registrar movimiento" subtitle="Registra un ingreso o una salida de tu almacén." [busy]="busy()" (closed)="closed.emit()">
@if(error()){<div class="notice error" role="alert">{{error()}}</div>}
@if(loading()){<div class="loading-row"><span class="spinner"></span>Cargando inventario…</div>}
<form id="movimiento-form" [formGroup]="form" (ngSubmit)="guardar()"><div class="form-column">
<div class="field-row"><label>TIPO DE MOVIMIENTO<select formControlName="tipoMovimiento"><option value="INGRESO">Ingreso de inventario</option><option value="SALIDA">Salida de inventario</option></select></label><label>TIPO DE INVENTARIO<select formControlName="tipoItem" (change)="form.controls.idItem.setValue(0)"><option value="PRODUCTO">Producto</option><option value="INSUMO">Insumo</option></select></label></div>
<label>PRODUCTO O INSUMO<select formControlName="idItem"><option [ngValue]="0">Selecciona un elemento</option>@for(i of opciones();track i.id){<option [ngValue]="i.id">{{codigo(i)}} · {{i.nombre}}{{i.activo?'':' (inactivo)'}}</option>}</select></label>
<div class="field-row"><label>CANTIDAD<input formControlName="cantidad" type="number" min="1" step="1"></label><label>ALMACÉN<input readonly [value]="seleccionado()?.almacen??'Se mostrará al seleccionar'"></label></div>
@if(seleccionado();as i){<div class="movement-preview"><div><strong>{{i.stock|numero}}</strong><small>Stock actual · {{i.unidad}}</small></div><app-icon name="chevron"/><div><strong [style.color]="nuevo()<0?'#bc252b':'#0754c5'">{{nuevo()|numero}}</strong><small>Stock después del movimiento</small></div></div>}
<label>MOTIVO<textarea rows="3" formControlName="motivo" maxlength="200" placeholder="Ej. Ingreso de producción o despacho de almacén"></textarea></label>
@if(form.invalid&&form.touched){<p class="field-error">Selecciona un elemento, ingresa una cantidad entera positiva y un motivo.</p>}
</div></form>
<div dialog-footer><button class="button secondary" [disabled]="busy()" (click)="closed.emit()">Cancelar</button><button class="button primary" form="movimiento-form" type="submit" [disabled]="busy()||loading()">{{busy()?'Registrando…':'Registrar movimiento'}}</button></div>
</app-dialog>`})
export class MovimientoForm implements OnInit {
  private readonly api=inject(InventarioApi);private readonly fb=inject(NonNullableFormBuilder);
  readonly closed=output<void>();readonly saved=output<Comprobante>();readonly busy=signal(false);readonly loading=signal(true);readonly error=signal('');readonly items=signal<Item[]>([]);readonly codigo=codigo;
  readonly form=this.fb.group({tipoMovimiento:['INGRESO'],tipoItem:['PRODUCTO'],idItem:[0,Validators.min(1)],cantidad:[1,[Validators.required,Validators.min(1),Validators.pattern(/^\d+$/)]],motivo:['',[Validators.required,Validators.maxLength(200)]]});
  private readonly valores=toSignal(this.form.valueChanges,{initialValue:this.form.getRawValue()});
  readonly opciones=computed(()=>this.items().filter(i=>i.tipoItem===this.valores().tipoItem&&(i.activo||this.valores().tipoMovimiento==='SALIDA')));
  readonly seleccionado=computed(()=>this.opciones().find(i=>i.id===this.valores().idItem));
  readonly nuevo=computed(()=>{const v=this.valores(),stock=this.seleccionado()?.stock??0;return stock+(v.tipoMovimiento==='SALIDA'?-1:1)*Number(v.cantidad??0);});
  async ngOnInit(){try{this.items.set(await this.api.todos<Item>('/stock'));}catch(e){this.error.set(mensajeError(e));}finally{this.loading.set(false);}}
  async guardar(){this.form.markAllAsTouched();if(this.form.invalid||this.busy())return;if(this.nuevo()<0){this.error.set('La cantidad de salida supera el stock disponible.');return;}if(!this.seleccionado()){this.error.set('Selecciona un elemento válido.');return;}const v=this.form.getRawValue();if(!v.motivo.trim()){this.error.set('Ingresa el motivo del movimiento.');return;}
    this.busy.set(true);this.error.set('');try{this.saved.emit(await this.api.post<Comprobante>('/movimientos',{...v,motivo:v.motivo.trim()}));}catch(e){this.error.set(mensajeError(e));}finally{this.busy.set(false);}
  }
}
