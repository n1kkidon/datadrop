import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './containers/login-form/login-form.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { RegisterFormComponent } from './containers/register-form/register-form.component';
import {GuestGuard} from "../core/guards/guest.guard";

const routes: Routes = [
  {
    path: 'login',
    component: LoginFormComponent,
    canActivate: [GuestGuard]
  },
  {
    path: 'register',
    component: RegisterFormComponent,
    canActivate: [GuestGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRoutingModule {}
