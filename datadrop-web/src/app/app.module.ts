import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BackEndUrlInterceptor } from './shared/interceptors/backend-url.interceptor';
import { UserModule } from './user/user.module';
import { MaterialModule } from './shared/modules/material/material.module';
import { ComponentsModule } from './shared/components/components.module';
import { AdminModule } from './admin/admin.module';
import { FilesModule } from './files/files.module';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './app-state';
import { EffectsModule } from '@ngrx/effects';
import { CoreModule } from './core/core.module';
import { SharingModule } from './sharing/sharing.module';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { PageNotFoundComponent } from './shared/components/page-not-found/page-not-found.component';
import {MAT_TOOLTIP_DEFAULT_OPTIONS} from "@angular/material/tooltip";

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgxPermissionsModule.forRoot(),
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    CoreModule,
    UserModule,
    AdminModule,
    ComponentsModule,
    FilesModule,
    SharingModule,
    AppRoutingModule,
    StoreModule.forRoot(reducers, {
      metaReducers,
    }),
    EffectsModule.forRoot([]),
    StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !isDevMode() }),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BackEndUrlInterceptor,
      multi: true,
    },
    {
      provide: MAT_TOOLTIP_DEFAULT_OPTIONS,
      useValue: {
        showDelay: 500
      }
    }
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
