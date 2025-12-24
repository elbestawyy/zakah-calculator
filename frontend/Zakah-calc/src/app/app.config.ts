import {ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import {authInterceptor} from './interceptors/auth-interceptor/auth-interceptor-interceptor';
import {refreshTokenInterceptor} from './interceptors/refreshTokenInterceptor/refresh-token-interceptor-interceptor';
import { error } from 'console';
import { errorInterceptor } from './interceptors/errorInterceptor/error-interceptor-interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withFetch(),
      withInterceptors([
        authInterceptor,
        refreshTokenInterceptor,
        errorInterceptor
      ])
    )
  ]
};
