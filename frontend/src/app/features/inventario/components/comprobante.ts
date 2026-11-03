import { Component, input, output } from '@angular/core';
import { Comprobante as ComprobanteModel } from '../../../core/models';
import { Dialog } from '../../../shared/dialog';import { Icon } from '../../../shared/icon';import { NumeroPipe,FechaPipe } from '../../../shared/format';
@Component({selector:'app-comprobante',imports:[Dialog,Icon,NumeroPipe,FechaPipe],template:`
<app-dialog title="Comprobante de movimiento" subtitle="Constancia del movimiento registrado en el inventario." size="small" (closed)="closed.emit()">
  @let c=comprobante();@let m=c.movimiento;
  <div class="receipt-brand"><div><strong>El Trujillano S.A.C.</strong><small>CONTROL DE INVENTARIO</small></div><div class="receipt-code"><b>{{m.codigoComprobante}}</b><small>{{m.codigoMovimiento}}</small></div></div>
  <dl class="definition-grid"><div><dt>TIPO DE MOVIMIENTO</dt><dd><span [class]="'badge '+(m.tipoMovimiento==='Ingreso'?'normal':'blue')">{{m.tipoMovimiento}}</span></dd></div><div><dt>FECHA Y HORA</dt><dd>{{m.hora|fecha:true}}</dd></div><div><dt>PRODUCTO O INSUMO</dt><dd><strong>{{m.nombreItem}}</strong></dd></div><div><dt>TIPO DE INVENTARIO</dt><dd>{{m.tipoItem}}</dd></div><div><dt>CANTIDAD</dt><dd>{{m.cantidad|numero}} {{m.unidad}}</dd></div><div><dt>ALMACÉN</dt><dd>{{m.almacen}}</dd></div><div><dt>SALDO ANTERIOR</dt><dd>{{m.stockAnterior|numero}} {{m.stockAnterior===null?'':m.unidad}}</dd></div><div><dt>SALDO POSTERIOR</dt><dd>{{m.stockNuevo|numero}} {{m.stockNuevo===null?'':m.unidad}}</dd></div><div><dt>REGISTRADO POR</dt><dd>{{c.responsable??'No registrado'}}</dd></div><div><dt>MOTIVO</dt><dd>{{c.motivo??'No registrado'}}</dd></div></dl>
  @if(m.stockAnterior===null){<div class="notice warning">Este movimiento histórico no tiene saldos guardados.</div>}
  <div class="receipt-result"><strong>{{m.tipoMovimiento==='Ingreso'?'+':'−'}}{{m.cantidad|numero}}</strong><p>{{m.unidad}} {{m.tipoMovimiento==='Ingreso'?'ingresadas al':'retiradas del'}} almacén</p></div>
  <div dialog-footer><button class="button secondary" (click)="closed.emit()">Cerrar</button><button class="button primary" (click)="imprimir()"><app-icon name="print"/>Imprimir</button></div>
</app-dialog>`})
export class ComprobanteDialog {readonly comprobante=input.required<ComprobanteModel>();readonly closed=output<void>();imprimir(){window.print();}}
