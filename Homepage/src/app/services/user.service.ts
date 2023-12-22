import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { DirectoryInfo } from '../models/DirectoryInfo';
import { UtilsService } from './utils.service';
import { NgxPermissionsService } from 'ngx-permissions';
import { FileInfo } from '../models/FileInfo';
import { UserDto } from '../models/UserDto';
import { AccountUpdateResponse } from '../models/AccountUpdateResponse';
import { AccoutnUpdateRequest } from '../models/AccountUpdateRequest';
import { SpaceUsageResponse } from '../models/SpaceUsageResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient, private utilsService: UtilsService, private permissionsService: NgxPermissionsService) { }

  logout(){
    localStorage.removeItem('token');
    localStorage.removeItem('rtoken');
    this.permissionsService.flushPermissions();
    this.permissionsService.loadPermissions(['GUEST']);
  }

  getUserById(id: number){
    return this.httpClient.get<UserDto>('/user/' + id, {observe: 'response', responseType: 'json'});
  }
  getUserByName(name: string){
    return this.httpClient.get<UserDto>('/user/name/' + name, {observe: 'response', responseType: 'json'});
  }

  getFilesSharedWithCurrentUser(sharingUserId: number){
    return this.httpClient.get<FileInfo[]>('/user/shared/files/' + sharingUserId, {observe: 'response', responseType: 'json'});
  }

  getDirectoriesSharedWithCurrentUser(sharingUserId: number){
    return this.httpClient.get<DirectoryInfo[]>('/user/shared/directories/' + sharingUserId, {observe: 'response', responseType: 'json'});
  }

  putAccountDetails(request: AccoutnUpdateRequest){
    return this.httpClient.put<AccountUpdateResponse>('/user/account', request, {observe: 'response', responseType: 'json'});
  }

  deleteUserAccount(){
    return this.httpClient.delete('/user/account', {observe: 'response', responseType: 'json'});
  }

  getSpaceUsageStatsOfCurrentUser(){
    return this.httpClient.get<SpaceUsageResponse>('/user/storage', {observe: 'response', responseType: 'json'});
  }

  getUsersSharingFiles(){
    return this.httpClient.get<UserDto[]>('/user/shared/users', {observe: 'response', responseType: 'json'});
  }
}
