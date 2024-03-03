import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './admin.component';
import { UsersTableComponent } from './containers/users-table/users-table.component';
import { MaterialModule } from '../shared/modules/material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxPermissionsModule } from 'ngx-permissions';
import { StoreModule } from '@ngrx/store';
import * as fromAdmin from './reducers/admin.reducer';
import { EffectsModule } from '@ngrx/effects';
import { AdminEffects } from './effects/admin.effects';

@NgModule({
  declarations: [AdminComponent, UsersTableComponent],
  imports: [
    CommonModule,
    AdminRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    NgxPermissionsModule,
    StoreModule.forFeature(fromAdmin.adminFeatureKey, fromAdmin.reducer),
    EffectsModule.forFeature([AdminEffects]),
  ],
})
export class AdminModule {}
