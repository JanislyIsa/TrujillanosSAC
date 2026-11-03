import { AfterViewInit, Component, ElementRef, HostListener, input, OnDestroy, output, viewChild } from '@angular/core';
import { Icon } from './icon';
@Component({selector:'app-dialog',imports:[Icon],template:`
<dialog #dialog [class]="'modal '+size()" [attr.aria-labelledby]="'dialog-title'" (cancel)="cancel($event)" (click)="backdrop($event)">
  <header class="modal-header"><div><h2 id="dialog-title">{{title()}}</h2>@if(subtitle()){<p>{{subtitle()}}</p>}</div>
    <button type="button" class="icon-button close" aria-label="Cerrar ventana" [disabled]="busy()" (click)="closed.emit()"><app-icon name="close"/></button>
  </header><div class="modal-body"><ng-content/></div>
  <footer class="modal-footer"><ng-content select="[dialog-footer]"/></footer>
</dialog>`})
export class Dialog implements AfterViewInit,OnDestroy {
  readonly title=input.required<string>();readonly subtitle=input('');readonly size=input('medium');readonly busy=input(false);readonly closed=output<void>();
  private readonly dialog=viewChild.required<ElementRef<HTMLDialogElement>>('dialog');
  private overflow='';
  ngAfterViewInit(){this.overflow=document.body.style.overflow;document.body.style.overflow='hidden';this.dialog().nativeElement.showModal();}
  ngOnDestroy(){document.body.style.overflow=this.overflow;}
  cancel(event:Event){event.preventDefault();if(!this.busy())this.closed.emit();}
  backdrop(event:MouseEvent){if(event.target===this.dialog().nativeElement){const r=this.dialog().nativeElement.getBoundingClientRect();if((event.clientX<r.left||event.clientX>r.right||event.clientY<r.top||event.clientY>r.bottom)&&!this.busy())this.closed.emit();}}
}
