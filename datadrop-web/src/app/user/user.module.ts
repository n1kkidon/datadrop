import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { UserComponent } from './user.component';
import { RegisterFormComponent } from './containers/register-form/register-form.component';
import { LoginFormComponent } from './containers/login-form/login-form.component';
import { MaterialModule } from '../shared/modules/material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxPermissionsModule } from 'ngx-permissions';
import { EffectsModule } from '@ngrx/effects';
import { UserEffects } from './effects/user.effects';
import { StoreModule } from '@ngrx/store';
import * as fromUser from '../user/reducers/user.reducer';

@NgModule({
  declarations: [UserComponent, RegisterFormComponent, LoginFormComponent],
  imports: [
    CommonModule,
    UserRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    NgxPermissionsModule,
    EffectsModule.forFeature([UserEffects]),
    StoreModule.forFeature(fromUser.userFeatureKey, fromUser.reducer),
  ],
})
export class UserModule {}
