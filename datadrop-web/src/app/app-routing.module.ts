import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './user/containers/login-form/login-form.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { FilesListComponent } from './files/containers/files-list/files-list.component';
import { UsersTableComponent } from './admin/containers/users-table/users-table.component';
import { RegisterFormComponent } from './user/containers/register-form/register-form.component';

const routes: Routes = [{ path: '**', redirectTo: 'home' }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
