import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventarioApi, mensajeError } from '../../../core/api';
import { Item, Pagina, TipoItem } from '../../../core/models';
import { Icon } from '../../../shared/icon';
import { Pager } from '../../../shared/pager';
import { codigo, NumeroPipe } from '../../../shared/format';
import { ItemDialog } from '../components/item-dialog';
@Component({imports:[FormsModule,Icon,Pager,NumeroPipe,ItemDialog],templateUrl:'./catalogo.html'})
export class CatalogoPage {
  private readonly api=inject(InventarioApi);
  readonly tipo=signal<TipoItem>('PRODUCTO');readonly data=signal<Pagina<Item>|null>(null);
  readonly busy=signal(false);readonly error=signal('');readonly success=signal('');
  readonly modo=signal<'create'|'edit'|'detail'|'delete'|null>(null);readonly seleccionado=signal<Item|null>(null);
  readonly codigo=codigo;buscar='';activo='';pagina=0;readonly tamanio=10;private secuencia=0;
  constructor(){void this.cargar();}
  async cargar(pagina=this.pagina){this.pagina=pagina;const orden=++this.secuencia;this.busy.set(true);this.error.set('');
    try{const data=await this.api.get<Pagina<Item>>(this.tipo()==='PRODUCTO'?'/productos':'/insumos',{buscar:this.buscar,pagina,tamanio:this.tamanio,activo:this.activo});if(orden===this.secuencia)this.data.set(data);}
    catch(e){if(orden===this.secuencia)this.error.set(mensajeError(e));}finally{if(orden===this.secuencia)this.busy.set(false);}
  }
  cambiar(tipo:TipoItem){this.tipo.set(tipo);this.buscar='';this.data.set(null);this.pagina=0;void this.cargar();}
  abrir(modo:'create'|'edit'|'detail'|'delete',item:Item|null=null){this.seleccionado.set(item);this.error.set('');this.modo.set(modo);}
  guardado(mensaje:string){this.modo.set(null);this.success.set(mensaje);void this.cargar(0);}
}
