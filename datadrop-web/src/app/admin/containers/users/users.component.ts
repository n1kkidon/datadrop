import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { UserDto } from '../../../shared/models/UserDto';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { UtilsService } from '../../../shared/services/utils.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit, AfterViewInit {
  constructor(
    private adminService: AdminService,
    private utilsService: UtilsService,
    private router: Router,
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
    this.fetchUsers();
  }

  fetchUsers() {
    this.adminService.getAllUsers().subscribe((response) => {
      if (response.ok) {
        this.userList = response.body!;
        this.dataSource.data = this.userList;
      }
    });
  }

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  deleteUser(user: UserDto) {
    this.utilsService
      .openConfirmActionDialog(
        'Are you sure you want to delete user "' +
          user.name +
          '" ? This action is irreversible.',
      )
      .afterClosed()
      .subscribe((rez: boolean) => {
        if (rez) {
          this.adminService
            .deleteUserAccountById(user.id)
            .subscribe((response) => {
              if (response.ok) {
                this.utilsService.openSnackBar(
                  'User "' + user.name + '" successfully deleted!',
                  'OK',
                );
                this.fetchUsers();
              }
            });
        }
      });
  }

  changeUserBlockState(user: UserDto) {
    this.adminService
      .changeBlockStateOfUserAccount(user.id)
      .subscribe((response) => {
        if (response.ok) {
          this.fetchUsers();
        }
      });
  }

  openUserRootDirectory(userId: number) {
    this.router.navigate(['/users/' + userId]);
  }
}
