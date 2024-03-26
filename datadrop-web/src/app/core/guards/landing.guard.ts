import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AppState } from '../../app-state';
import { Injectable } from '@angular/core';
import { map, Observable, take } from 'rxjs';
import { selectIsUserAuthenticated } from '../../user/reducers/user.reducer';
import {NgxPermissionsService} from "ngx-permissions";

@Injectable({
  providedIn: 'root',
})
export class LandingGuard {
  constructor(
    private store: Store<AppState>,
    private router: Router,
    private ngxPermissions: NgxPermissionsService
  ) {}

  canActivate(): Observable<boolean> {
    return this.store.select(selectIsUserAuthenticated).pipe(
      take(1),
      map((authenticated) => {
        if (authenticated) this.router.navigate(['storage']);
        else {
          this.ngxPermissions.loadPermissions(['GUEST']);
          this.router.navigate(['login']);
        }

        return authenticated;
      }),
    );
  }
}
