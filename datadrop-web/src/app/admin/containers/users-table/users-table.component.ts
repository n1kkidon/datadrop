import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { UserDto } from '../../../shared/models/UserDto';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { UtilsService } from '../../../shared/services/utils.service';
import { Router } from '@angular/router';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';
import { selectUserList } from '../../reducers/admin.reducer';
import { filter, map, takeUntil } from 'rxjs';
import * as AdminActions from '../../actions/admin.actions';
import { openConfirmDialog } from '../../../core/actions/core.actions';

@Component({
  selector: 'app-users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.scss'],
})
export class UsersTableComponent implements OnInit, AfterViewInit {
  constructor(
    private router: Router,
    private store: Store<AppState>,
  ) {}

  userList: UserDto[] = [];

  displayedColumns: string[] = [
    'id',
    'name',
    'email',
    'creationDate',
    'state',
    'actions',
  ];
  dataSource = new MatTableDataSource<UserDto>(this.userList);

  ngOnInit(): void {
    this.store
      .select(selectUserList)
      .pipe(
        filter((result) => result !== null),
        map((result) => (this.dataSource.data = result)),
      )
      .subscribe();
    this.store.dispatch(AdminActions.loadUserList());
  }

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  deleteUser(user: UserDto) {
    this.store.dispatch(
      openConfirmDialog({
        dialogText: `Are you sure you want to delete user "${user.name}"? This action is irreversible.`,
        action: AdminActions.deleteUserAccount({ id: user.id }),
      }),
    );
  }

  changeUserBlockState(user: UserDto) {
    this.store.dispatch(
      AdminActions.changeUserAccountBlockState({ id: user.id }),
    );
  }

  openUserRootDirectory(userId: number) {
    this.router.navigate(['/users/' + userId]);
  }
}
