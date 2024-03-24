import { Component, OnInit } from '@angular/core';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';
import { selectSharingUserList } from '../../reducers/sharing.reducer';
import { Observable } from 'rxjs';
import { UserDto } from '../../../shared/models/UserDto';
import * as SharingActions from '../../actions/sharing.actions';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss',
})
export class UsersListComponent implements OnInit {
  userList$: Observable<UserDto[]> = new Observable();

  constructor(private store: Store<AppState>) {}

  ngOnInit(): void {
    this.userList$ = this.store.select(selectSharingUserList);
    this.store.dispatch(SharingActions.loadUsersSharingFiles());
  }
}
