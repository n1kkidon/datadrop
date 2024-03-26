import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';
import { selectSharingUserList } from '../../reducers/sharing.reducer';
import { filter, map, Observable, switchMap } from 'rxjs';
import { UserDto } from '../../../shared/models/UserDto';
import * as SharingActions from '../../actions/sharing.actions';
import { MatTableDataSource } from '@angular/material/table';
import { selectUserList } from '../../../admin/reducers/admin.reducer';
import * as AdminActions from '../../../admin/actions/admin.actions';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss',
})
export class UsersListComponent implements OnInit, AfterViewInit {
  constructor(private store: Store<AppState>) {}

  ngOnInit(): void {
    this.store
      .select(selectSharingUserList)
      .pipe(
        filter((result) => result !== null),
        switchMap((result) => (this.dataSource.data = result)),
      )
      .subscribe();

    this.store.dispatch(SharingActions.loadUsersSharingFiles());
  }

  displayedColumns: string[] = ['name', 'email', 'actions'];
  dataSource = new MatTableDataSource<UserDto>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
