import { Observable} from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { SpaceUsageModel } from '../../models/SpaceUsageModel';
import * as UserActions from '../../../user/actions/user.actions';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';
import {selectUserSpaceUsage} from "../../../user/reducers/user.reducer";

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
})
export class SidenavComponent implements OnInit {
  constructor(
    private store: Store<AppState>,
  ) {}

  spaceUsage$: Observable<SpaceUsageModel|null> = new Observable();

  ngOnInit() {
    this.spaceUsage$ = this.store.select(selectUserSpaceUsage);
    this.store.dispatch(UserActions.loadSpaceUsageStats());
  }

  onLogout() {
    this.store.dispatch(UserActions.logout());
  }
}
