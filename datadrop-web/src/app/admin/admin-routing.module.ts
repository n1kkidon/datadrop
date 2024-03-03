import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersComponent } from './containers/users/users.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { FilesListComponent } from '../files/containers/files-list/files-list.component';

const routes: Routes = [
  {
    path: 'users',
    component: UsersComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'users/:userId',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'users/:userId/:id',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_ADMIN'],
      },
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
