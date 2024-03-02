import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomepageComponent } from './containers/homepage/homepage.component';
import { NgxPermissionsGuard } from 'ngx-permissions';

const routes: Routes = [
  {
    path: 'home',
    component: HomepageComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['GUEST', 'ROLE_USER', 'ROLE_ADMIN'],
      },
    },
    children: [{ path: '', component: HomepageComponent, pathMatch: 'full' }],
  },

  {
    path: 'home/:id',
    component: HomepageComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_USER', 'ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'shared/:shareUserId',
    component: HomepageComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_USER', 'ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'shared/:shareUserId/:id',
    component: HomepageComponent,
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
