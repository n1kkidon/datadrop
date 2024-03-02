import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './user/containers/login-form/login-form.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { HomepageComponent } from './files/containers/homepage/homepage.component';
import { UsersComponent } from './admin/containers/users/users.component';
import { RegisterFormComponent } from './user/containers/register-form/register-form.component';

const routes: Routes = [{ path: '**', redirectTo: 'home' }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
