import { HttpClient, HttpErrorResponse, HttpEventType, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subscription, catchError, finalize, map } from 'rxjs';
import { DirectoryInfo } from '../models/DirectoryInfo';
import { FileInfo } from '../models/FileInfo';
import { UtilsService } from './utils.service';
import { saveAs } from 'file-saver';
import { ShareStateUpdateRequest } from '../models/ShareStateUpdateRequest';
import { UserDto } from '../models/UserDto';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(private httpClient: HttpClient, private utilsService: UtilsService) { }

  getFileInfo(index: number) : Observable<HttpResponse<FileInfo> | HttpErrorResponse>{
    return this.httpClient.get<FileInfo>('/file/download/' + index + '/info', {observe: 'response', responseType: 'json'});
  }

  downloadFile(index: number){
    return this.httpClient.get('/file/download/' + index, {observe: 'response', responseType: 'blob'});
  }

  uploadFile(uploadDirectoryId: number, file: Blob, sharedState: string){
    const fd = new FormData();
    fd.append('file', file);
    fd.append('uploadDirectoryId', uploadDirectoryId+"");
    fd.append('sharedState', sharedState);
    return this.httpClient.post<FileInfo>('/file/upload', fd, { reportProgress: true, observe: 'events', responseType: 'json'});
  }

  renameFile(itemId: number, newName: string){
    return this.httpClient.patch<FileInfo>('/file/rename', {itemId, newName}, {observe: 'response', responseType: 'json'});
  }

  deleteFileById(itemId: number){
    return this.httpClient.delete('/file/' + itemId, {observe: 'response', responseType: 'json'});
  }

  changeFileShareState(updateRequest: ShareStateUpdateRequest){
    return this.httpClient.patch('/file/share', updateRequest, {observe: 'response', responseType: 'json'});
  }

  getUsersFileIsSharedWith(fileId: number){
    return this.httpClient.get<UserDto[]>('/file/shared/' + fileId, {observe: 'response', responseType: 'json'});
  }
}
