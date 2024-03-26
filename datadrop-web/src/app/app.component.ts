import { Component } from '@angular/core';
import {Store} from "@ngrx/store";
import {AppState} from "./app-state";
import {selectIsUserAuthenticated} from "./user/reducers/user.reducer";
import {Observable} from "rxjs";
import * as UserActions from './user/actions/user.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'DataDrop';
  isAuthenticated$: Observable<boolean> = new Observable();

  constructor(store: Store<AppState>) {
    this.isAuthenticated$ = store.select(selectIsUserAuthenticated);
    store.dispatch(UserActions.onLoad());
  }
}
