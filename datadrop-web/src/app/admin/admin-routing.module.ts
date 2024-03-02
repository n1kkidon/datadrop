import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersComponent } from './containers/users/users.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { HomepageComponent } from '../files/containers/homepage/homepage.component';

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
    component: HomepageComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['ROLE_ADMIN'],
      },
    },
  },

  {
    path: 'users/:userId/:id',
    component: HomepageComponent,
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
