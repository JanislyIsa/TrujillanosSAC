import { Component, OnInit } from '@angular/core';
// Compatibilidad con enlaces antiguos: toda autenticacion usa el login general.
@Component({selector:'app-login',template:'<p>Abriendo el inicio de sesión… <a href="/login">Continuar</a></p>'})
export class Login implements OnInit {
  ngOnInit():void { window.location.replace('/login'); }
}
