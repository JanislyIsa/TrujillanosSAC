import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withXsrfConfiguration, withInterceptors } from '@angular/common/http';
import { provideRouter, withHashLocation } from '@angular/router';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { sesionInterceptor } from './app/core/auth';
bootstrapApplication(App, {
  providers: [
    provideHttpClient(withInterceptors([sesionInterceptor]),withXsrfConfiguration({ cookieName: 'XSRF-TOKEN', headerName: 'X-XSRF-TOKEN' })),
    provideRouter(routes, withHashLocation()),
  ],
}).catch(error => console.error('No se pudo iniciar inventario', error));
