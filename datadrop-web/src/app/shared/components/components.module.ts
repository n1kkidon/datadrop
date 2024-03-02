import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../modules/material/material.module';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavbarComponent } from './navbar/navbar.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { NgxPermissionsModule } from 'ngx-permissions';

@NgModule({
  declarations: [ConfirmDialogComponent, NavbarComponent, SidenavComponent],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    NgxPermissionsModule,
  ],
  exports: [ConfirmDialogComponent, NavbarComponent, SidenavComponent],
})
export class ComponentsModule {}
