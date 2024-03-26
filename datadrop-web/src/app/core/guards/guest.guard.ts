import {Router} from '@angular/router';
import {Injectable} from "@angular/core";
import {Store} from "@ngrx/store";
import {AppState} from "../../app-state";
import {NgxPermissionsService} from "ngx-permissions";
import {map, Observable, take} from "rxjs";
import {selectIsUserAuthenticated} from "../../user/reducers/user.reducer";

@Injectable({
  providedIn: 'root',
})
export class GuestGuard {
  constructor(
    private store: Store<AppState>,
    private router: Router,
    private ngxPermissions: NgxPermissionsService
  ) {}

  canActivate(): Observable<boolean> {
    return this.store.select(selectIsUserAuthenticated).pipe(
      take(1),
      map((authenticated) => {
        if (authenticated)
          this.router.navigate(['storage']);
        else {
          this.ngxPermissions.loadPermissions(['GUEST']);
        }

        return !authenticated;
      }),
    );
  }
}
