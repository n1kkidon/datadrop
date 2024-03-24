import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserDto } from '../../shared/models/UserDto';
import { SpaceUsageModel } from '../../shared/models/SpaceUsageModel';
import { DirectoryDto } from '../../shared/models/DirectoryDto';
import { FileDto } from '../../shared/models/FileDto';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  constructor(private httpClient: HttpClient) {}

  changeBlockStateOfUserAccount(id: number) {
    return this.httpClient.patch<UserDto>(
      '/admin/block/' + id,
      {},
      { observe: 'response', responseType: 'json' },
    );
  }

  getAllUsers() {
    return this.httpClient.get<UserDto[]>('/admin/all', {
      observe: 'response',
      responseType: 'json',
    });
  }

  deleteUserAccountById(id: number) {
    return this.httpClient.delete('/admin/account/' + id, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUserRootDirectoryByUserId(id: number) {
    return this.httpClient.get<DirectoryDto>('/admin/user/' + id + '/root', {
      observe: 'response',
      responseType: 'json',
    });
  }

  /*
  getDirectoriesSharedWithUser(id: number) {
    return this.httpClient.get<DirectoryInfo[]>(
      '/admin/shared/directories/' + id,
      { observe: 'response', responseType: 'json' },
    );
  }

  getFilesSharedWithUser(id: number) {
    return this.httpClient.get<FileInfo[]>('/admin/shared/files/' + id, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getSpaceUsageStatsByUserId(id: number) {
    return this.httpClient.get<SpaceUsageResponse>('/admin/storage/' + id, {
      observe: 'response',
      responseType: 'json',
    });
  }

   */
}
