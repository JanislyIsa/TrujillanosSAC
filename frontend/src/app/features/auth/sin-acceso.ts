import { Component } from '@angular/core';import { RouterLink } from '@angular/router';import { Icon } from '../../shared/icon';
@Component({imports:[RouterLink,Icon],template:`<section class="empty-state"><app-icon name="shield"/><h1>Acceso restringido</h1><p>Tu cuenta no tiene permiso para abrir esta sección.</p><a class="button primary" routerLink="/inventario/stock">Volver al inventario</a></section>`})
export class SinAcceso{}
