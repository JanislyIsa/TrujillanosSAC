import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { InventarioApi, mensajeError } from '../../../core/api';
import { Catalogos, Item, TipoItem } from '../../../core/models';
import { Dialog } from '../../../shared/dialog';
import { Icon } from '../../../shared/icon';
import { codigo, estadoLabel, NumeroPipe } from '../../../shared/format';
@Component({selector:'app-item-dialog',imports:[Dialog,Icon,NumeroPipe,ReactiveFormsModule],templateUrl:'./item-dialog.html'})
export class ItemDialog implements OnInit {
  readonly tipo=input.required<TipoItem>();readonly modo=input.required<'create'|'edit'|'detail'|'delete'>();
  readonly item=input<Item|null>(null);readonly closed=output<void>();readonly saved=output<string>();
  private readonly api=inject(InventarioApi);private readonly fb=inject(NonNullableFormBuilder);
  readonly busy=signal(false);readonly loading=signal(false);readonly error=signal('');readonly camposApi=signal<Record<string,string>>({});
  readonly opciones=signal<Catalogos>({categorias:[],presentaciones:[],unidadesMedida:[],almacenes:[]});
  readonly preview=signal('');private archivo:File|null=null;private previewLocal:string|null=null;
  readonly esProducto=computed(()=>this.tipo()==='PRODUCTO');readonly esEdicion=computed(()=>this.modo()==='edit');
  readonly sustantivo=computed(()=>this.esProducto()?'producto':'insumo');
  readonly titulo=computed(()=>({create:'Registrar ',edit:'Editar ',detail:'Detalle del ',delete:'Eliminar '}[this.modo()])+this.sustantivo());
  readonly codigo=codigo;readonly estadoLabel=estadoLabel;
  readonly form=this.fb.group({
    nombre:['',[Validators.required,Validators.maxLength(100)]],descripcion:['',[Validators.required,Validators.maxLength(255)]],
    precio:[0.01,[Validators.required,Validators.min(0.01),Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
    stock:[0,[Validators.required,Validators.min(0),Validators.max(2147483647),Validators.pattern(/^\d+$/)]],
    stockMinimo:[0,[Validators.required,Validators.min(0),Validators.pattern(/^\d+$/)]],
    stockLimite:[0,[Validators.required,Validators.min(0),Validators.pattern(/^\d+$/)]],
    idCategoria:[0,Validators.min(1)],idUnidad:[0,[Validators.required,Validators.min(1)]],
    idAlmacen:[0,[Validators.required,Validators.min(1)]],activo:[true],
  });
  async ngOnInit(){
    const item=this.item();
    if(item){this.form.patchValue({nombre:item.nombre,descripcion:item.descripcion??'',precio:item.precio??0.01,stock:item.stock,stockMinimo:item.stockMinimo,stockLimite:item.stockLimite,idCategoria:item.idCategoria??0,idUnidad:item.idUnidad,idAlmacen:item.idAlmacen,activo:item.activo});this.preview.set(item.imagenUrl??'');}
    if(this.esEdicion()){this.form.controls.idUnidad.disable();this.form.controls.idAlmacen.disable();}
    if(!this.esProducto()){this.form.controls.descripcion.clearValidators();this.form.controls.idCategoria.clearValidators();this.form.controls.precio.clearValidators();this.form.controls.descripcion.updateValueAndValidity();this.form.controls.idCategoria.updateValueAndValidity();this.form.controls.precio.updateValueAndValidity();}
    if(this.modo()==='create'||this.modo()==='edit'){
      this.loading.set(true);try{this.opciones.set(await this.api.get<Catalogos>('/catalogos'));}catch(e){this.error.set(mensajeError(e));}finally{this.loading.set(false);}
    }
  }
  campo(nombre:keyof typeof this.form.controls):string{
    const c=this.form.controls[nombre];if(c.touched&&c.invalid){
      if(c.hasError('required')||c.hasError('min'))return nombre==='nombre'||nombre==='descripcion'?'Este campo es obligatorio.':nombre.startsWith('id')?'Selecciona una opción.':'Ingresa un valor válido.';
      if(c.hasError('maxlength'))return 'El texto supera el máximo permitido.';
      if(c.hasError('pattern'))return nombre==='precio'?'Usa hasta dos decimales.':'Ingresa un número entero.';
      if(c.hasError('max'))return 'La cantidad es demasiado grande.';
    }return this.camposApi()[nombre]??'';
  }
  imagen(event:Event){const input=event.target as HTMLInputElement,file=input.files?.[0];if(!file)return;
    if(!['image/jpeg','image/png'].includes(file.type)||file.size>2*1024*1024){this.error.set('Selecciona una imagen JPG o PNG de hasta 2 MB.');input.value='';return;}
    if(this.previewLocal)URL.revokeObjectURL(this.previewLocal);this.archivo=file;this.previewLocal=URL.createObjectURL(file);this.preview.set(this.previewLocal);this.error.set('');
  }
  cerrar(){if(this.previewLocal)URL.revokeObjectURL(this.previewLocal);this.closed.emit();}
  async guardar(){
    this.form.markAllAsTouched();if(this.form.invalid||this.busy()||this.loading())return;
    const v=this.form.getRawValue();if(Number(v.stockLimite)<Number(v.stockMinimo)){this.error.set('El stock límite debe ser mayor o igual al mínimo.');return;}
    if(!v.nombre.trim()||(this.esProducto()&&!v.descripcion.trim())){this.error.set('Completa los campos de texto.');return;}
    this.busy.set(true);this.error.set('');this.camposApi.set({});
    try{
      let imagenRuta=this.item()?.imagenUrl??null;
      if(this.archivo){imagenRuta=await this.api.subir(this.archivo);this.archivo=null;this.preview.set(imagenRuta);}
      const body:Record<string,unknown>={nombre:v.nombre.trim(),stockMinimo:Number(v.stockMinimo),stockLimite:Number(v.stockLimite),idAlmacen:Number(v.idAlmacen),activo:v.activo};
      if(!this.esEdicion())body['stock']=Number(v.stock);
      if(this.esProducto())Object.assign(body,{descripcion:v.descripcion.trim(),precio:Number(v.precio),idCategoria:Number(v.idCategoria),idPresentacion:Number(v.idUnidad),imagenRuta});
      else body['idUnidadMedida']=Number(v.idUnidad);
      const ruta=this.esProducto()?'/productos':'/insumos';
      if(this.esEdicion())await this.api.put(ruta+'/'+this.item()!.id,body);else await this.api.post(ruta,body);
      if(this.previewLocal)URL.revokeObjectURL(this.previewLocal);
      this.saved.emit((this.esProducto()?'Producto':'Insumo')+(this.esEdicion()?' actualizado correctamente.':' registrado correctamente.'));
    }catch(e){this.error.set(mensajeError(e));if(e instanceof HttpErrorResponse)this.camposApi.set(e.error?.errores??{});}
    finally{this.busy.set(false);}
  }
  async eliminar(){if(this.busy())return;this.busy.set(true);this.error.set('');
    try{await this.api.delete((this.esProducto()?'/productos/':'/insumos/')+this.item()!.id);this.saved.emit('El registro quedó inactivo. Se conservaron las existencias y el historial.');}
    catch(e){this.error.set(mensajeError(e));}finally{this.busy.set(false);}
  }
}
