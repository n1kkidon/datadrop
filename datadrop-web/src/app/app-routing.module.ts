import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './login-form/login-form.component';
import { NgxPermissionsGuard } from 'ngx-permissions';
import { HomepageComponent } from './homepage/homepage.component';
import { UsersComponent } from './users/users.component';
import { RegisterFormComponent } from './register-form/register-form.component';

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

  {
    path: 'home',
    component: HomepageComponent,
    canActivate: [NgxPermissionsGuard],
    data: {
      permissions: {
        only: ['GUEST', 'ROLE_USER', 'ROLE_ADMIN'],
      },
    },
    children: [
      { path: '', component: HomepageComponent, pathMatch: 'full' },
    ],
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

  { path: '**', redirectTo: 'home' },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {




}
