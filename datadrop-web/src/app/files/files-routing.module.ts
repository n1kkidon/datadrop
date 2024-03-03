import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FilesListComponent } from './containers/files-list/files-list.component';
import { NgxPermissionsGuard } from 'ngx-permissions';

const routes: Routes = [
  {
    path: 'home',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['GUEST', 'ROLE_USER', 'ROLE_ADMIN'],
      },
    },
    children: [{ path: '', component: FilesListComponent, pathMatch: 'full' }],
  },

  {
    path: 'home/:id',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_USER', 'ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'shared/:shareUserId',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_USER', 'ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'shared/:shareUserId/:id',
    component: FilesListComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_USER', 'ROLE_ADMIN'],
      },
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FilesRoutingModule {}
