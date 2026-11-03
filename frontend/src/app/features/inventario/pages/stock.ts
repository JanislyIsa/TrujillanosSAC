import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InventarioApi,mensajeError } from '../../../core/api';
import { AuthService } from '../../../core/auth';
import { Catalogos,Item,Pagina } from '../../../core/models';
import { Icon } from '../../../shared/icon';
import { Pager } from '../../../shared/pager';
import { codigo,estadoLabel,NumeroPipe } from '../../../shared/format';
import { ItemDialog } from '../components/item-dialog';
@Component({imports:[FormsModule,RouterLink,Icon,Pager,NumeroPipe,ItemDialog],templateUrl:'./stock.html'})
export class StockPage {
  readonly auth=inject(AuthService);private readonly api=inject(InventarioApi);
  readonly data=signal<Pagina<Item>|null>(null);readonly todos=signal<Item[]>([]);readonly almacenes=signal<Catalogos['almacenes']>([]);
  readonly busy=signal(false);readonly error=signal('');readonly detalle=signal<Item|null>(null);
  readonly bajo=computed(()=>this.todos().filter(i=>i.estadoStock==='BAJO').length);
  readonly sobre=computed(()=>this.todos().filter(i=>i.estadoStock==='SOBRESTOCK').length);
  readonly codigo=codigo;readonly estadoLabel=estadoLabel;
  buscar='';tipoItem='';estado='';idAlmacen='';pagina=0;readonly tamanio=10;private secuencia=0;
  constructor(){void this.iniciar();}
  async iniciar(){try{const [items,catalogos]=await Promise.all([this.api.todos<Item>('/stock'),this.api.get<Catalogos>('/catalogos')]);this.todos.set(items);this.almacenes.set(catalogos.almacenes);}catch(e){this.error.set(mensajeError(e));}await this.cargar();}
  async cargar(pagina=this.pagina){this.pagina=pagina;const orden=++this.secuencia;this.busy.set(true);this.error.set('');
    try{const data=await this.api.get<Pagina<Item>>('/stock',{pagina,tamanio:this.tamanio,buscar:this.buscar,tipoItem:this.tipoItem,estado:this.estado,idAlmacen:this.idAlmacen});if(orden===this.secuencia)this.data.set(data);}
    catch(e){if(orden===this.secuencia)this.error.set(mensajeError(e));}finally{if(orden===this.secuencia)this.busy.set(false);}
  }
}
