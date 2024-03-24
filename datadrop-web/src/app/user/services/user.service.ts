import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DirectoryDto } from '../../shared/models/DirectoryDto';
import { NgxPermissionsService } from 'ngx-permissions';
import { FileDto } from '../../shared/models/FileDto';
import { UserDto } from '../../shared/models/UserDto';
import { AccountUpdateResponse } from '../../shared/models/response/AccountUpdateResponse';
import { AccountUpdateRequest } from '../../shared/models/request/AccountUpdateRequest';
import { SpaceUsageResponse } from '../../shared/models/response/SpaceUsageResponse';
import { Token } from '../../shared/models/Token';
import { jwtDecode } from 'jwt-decode';
import { AuthenticationModel } from '../../shared/models/AuthenticationModel';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(
    private httpClient: HttpClient,
    private permissionsService: NgxPermissionsService,
  ) {}

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('rtoken');
    this.permissionsService.flushPermissions();
    this.permissionsService.loadPermissions(['GUEST']);
  }

  userLoggedIn(auth: AuthenticationModel) {
    localStorage.setItem('token', auth.token);
    localStorage.setItem('rtoken', auth.refreshToken);
    let token: Token = jwtDecode(auth.token);
    this.permissionsService.loadPermissions([token.roles[0].authority]);
  }

  getUserById(id: number) {
    return this.httpClient.get<UserDto>('/user/' + id, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUserByName(name: string) {
    return this.httpClient.get<UserDto>('/user/name/' + name, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getFilesSharedWithCurrentUser(sharingUserId: number) {
    return this.httpClient.get<FileDto[]>(
      '/user/shared/files/' + sharingUserId,
      { observe: 'response', responseType: 'json' },
    );
  }

  getDirectoriesSharedWithCurrentUser(sharingUserId: number) {
    return this.httpClient.get<DirectoryDto[]>(
      '/user/shared/directories/' + sharingUserId,
      { observe: 'response', responseType: 'json' },
    );
  }

  putAccountDetails(request: AccountUpdateRequest) {
    return this.httpClient.put<AccountUpdateResponse>(
      '/user/account',
      request,
      { observe: 'response', responseType: 'json' },
    );
  }

  deleteUserAccount() {
    return this.httpClient.delete('/user/account', {
      observe: 'response',
      responseType: 'json',
    });
  }

  getSpaceUsageStatsOfCurrentUser() {
    return this.httpClient.get<SpaceUsageResponse>('/user/storage', {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUsersSharingFiles() {
    return this.httpClient.get<UserDto[]>('/user/shared/users', {
      observe: 'response',
      responseType: 'json',
    });
  }
}
