import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './containers/login-form/login-form.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { RegisterFormComponent } from './containers/register-form/register-form.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginFormComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: 'GUEST',
      },
    },
  },
  {
    path: 'register',
    component: RegisterFormComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: 'GUEST',
      },
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRoutingModule {}
