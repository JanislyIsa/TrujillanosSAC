import { Component,inject,signal } from '@angular/core';import { FormsModule } from '@angular/forms';
import { InventarioApi,mensajeError } from '../../../core/api';import { Comprobante,Movimiento,Pagina } from '../../../core/models';
import { Icon } from '../../../shared/icon';import { Pager } from '../../../shared/pager';import { FechaPipe,NumeroPipe } from '../../../shared/format';
import { MovimientoForm } from '../components/movimiento-form';import { ComprobanteDialog } from '../components/comprobante';
@Component({imports:[FormsModule,Icon,Pager,FechaPipe,NumeroPipe,MovimientoForm,ComprobanteDialog],templateUrl:'./movimientos.html'})
export class MovimientosPage{
  private readonly api=inject(InventarioApi);readonly data=signal<Pagina<Movimiento>|null>(null);readonly busy=signal(false);readonly error=signal('');readonly success=signal('');
  readonly registrar=signal(false);readonly comprobante=signal<Comprobante|null>(null);readonly consultando=signal<number|null>(null);
  tipoMovimiento='';tipoItem='';desde='';hasta='';pagina=0;readonly tamanio=10;private secuencia=0;
  constructor(){void this.cargar();}
  async cargar(pagina=this.pagina){if(this.desde&&this.hasta&&this.desde>this.hasta){this.error.set('La fecha de inicio no puede ser posterior a la fecha final.');return;}this.pagina=pagina;const orden=++this.secuencia;this.busy.set(true);this.error.set('');
    try{const data=await this.api.get<Pagina<Movimiento>>('/movimientos',{pagina,tamanio:this.tamanio,tipoMovimiento:this.tipoMovimiento,tipoItem:this.tipoItem,desde:this.desde,hasta:this.hasta});if(orden===this.secuencia)this.data.set(data);}catch(e){if(orden===this.secuencia)this.error.set(mensajeError(e));}finally{if(orden===this.secuencia)this.busy.set(false);}
  }
  async ver(id:number){if(this.consultando())return;this.consultando.set(id);try{this.comprobante.set(await this.api.get<Comprobante>('/movimientos/'+id+'/comprobante'));}catch(e){this.error.set(mensajeError(e));}finally{this.consultando.set(null);}}
  guardado(c:Comprobante){this.registrar.set(false);this.comprobante.set(c);this.success.set('Movimiento registrado. Las existencias se actualizaron correctamente.');void this.cargar(0);}
}
