import { Pipe, PipeTransform } from '@angular/core';
import { EstadoStock, Item } from '../core/models';
export function codigo(item:Pick<Item,'tipoItem'|'id'>){return (item.tipoItem==='PRODUCTO'?'PROD':'INS')+'-'+String(item.id).padStart(3,'0');}
export function estadoLabel(estado:EstadoStock|string){return ({BAJO:'Stock bajo',NORMAL:'Normal',SOBRESTOCK:'Sobrestock'} as Record<string,string>)[estado]??estado;}
@Pipe({name:'numero'})export class NumeroPipe implements PipeTransform {transform(valor:number|null|undefined):string{return valor==null?'—':new Intl.NumberFormat('es-PE',{maximumFractionDigits:2}).format(valor);}}
@Pipe({name:'fecha'})export class FechaPipe implements PipeTransform {transform(valor:string|null|undefined,conHora=false):string{if(!valor)return '—';const fecha=new Date(valor.length===10?valor+'T12:00:00':valor);return new Intl.DateTimeFormat('es-PE',{day:'2-digit',month:'2-digit',year:'numeric',...(conHora?{hour:'2-digit' as const,minute:'2-digit' as const}:{})}).format(fecha);}}
