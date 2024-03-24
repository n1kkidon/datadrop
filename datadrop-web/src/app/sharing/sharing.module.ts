import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharingRoutingModule } from './sharing-routing.module';
import { SharingComponent } from './sharing.component';
import { MaterialModule } from '../shared/modules/material/material.module';
import { UsersListComponent } from './containers/users-list/users-list.component';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import * as fromSharing from '../sharing/reducers/sharing.reducer';
import { SharingEffects } from './effects/sharing.effects';

@NgModule({
  declarations: [SharingComponent, UsersListComponent],
  imports: [
    CommonModule,
    MaterialModule,
    SharingRoutingModule,
    EffectsModule.forFeature([SharingEffects]),
    StoreModule.forFeature(fromSharing.sharingFeatureKey, fromSharing.reducer),
  ],
})
export class SharingModule {}
