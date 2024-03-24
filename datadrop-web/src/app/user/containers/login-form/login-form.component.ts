import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';
import * as UserActions from '../../actions/user.actions';
import { selectUserAuth } from '../../reducers/user.reducer';
import { filter, take } from 'rxjs';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent {
  constructor(
    private store: Store<AppState>,
    private router: Router,
  ) {}

  hide = true;

  onSubmit(username: string, pass: string) {
    this.store.dispatch(
      UserActions.login({ credentials: { username, password: pass } }),
    );
    this.store
      .select(selectUserAuth)
      .pipe(
        filter((x) => x !== null),
        take(1),
      )
      .subscribe(() => this.router.navigate(['/home']));
  }
}
