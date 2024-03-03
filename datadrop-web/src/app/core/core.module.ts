import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './effects/core.effects';
import { MaterialModule } from '../shared/modules/material/material.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MaterialModule,
    EffectsModule.forFeature([CoreEffects]),
  ],
})
export class CoreModule {}
