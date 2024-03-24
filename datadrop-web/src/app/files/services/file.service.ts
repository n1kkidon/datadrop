import {
  HttpClient,
  HttpErrorResponse,
  HttpEventType,
  HttpResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subscription, catchError, finalize, map } from 'rxjs';
import { DirectoryDto } from '../../shared/models/DirectoryDto';
import { FileDto } from '../../shared/models/FileDto';
import { UtilsService } from '../../shared/services/utils.service';
import { saveAs } from 'file-saver';
import { ShareStateUpdateRequest } from '../../shared/models/request/ShareStateUpdateRequest';
import { UserDto } from '../../shared/models/UserDto';

@Injectable({
  providedIn: 'root',
})
export class FileService {
  constructor(
    private httpClient: HttpClient,
    private utilsService: UtilsService,
  ) {}

  getFileInfo(
    index: number,
  ): Observable<HttpResponse<FileDto> | HttpErrorResponse> {
    return this.httpClient.get<FileDto>('/file/download/' + index + '/info', {
      observe: 'response',
      responseType: 'json',
    });
  }

  downloadFile(index: number) {
    return this.httpClient.get('/file/download/' + index, {
      observe: 'response',
      responseType: 'blob',
    });
  }

  uploadFile(uploadDirectoryId: number, file: Blob, sharedState: string) {
    const fd = new FormData();
    fd.append('file', file);
    fd.append('uploadDirectoryId', uploadDirectoryId + '');
    fd.append('sharedState', sharedState);
    return this.httpClient.post<FileDto>('/file/upload', fd, {
      reportProgress: true,
      observe: 'events',
      responseType: 'json',
    });
  }

  renameFile(itemId: number, newName: string) {
    return this.httpClient.patch<FileDto>(
      '/file/rename',
      { itemId, newName },
      { observe: 'response', responseType: 'json' },
    );
  }

  deleteFileById(itemId: number) {
    return this.httpClient.delete('/file/' + itemId, {
      observe: 'response',
      responseType: 'json',
    });
  }

  changeFileShareState(updateRequest: ShareStateUpdateRequest) {
    return this.httpClient.patch('/file/share', updateRequest, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUsersFileIsSharedWith(fileId: number) {
    return this.httpClient.get<UserDto[]>('/file/shared/' + fileId, {
      observe: 'response',
      responseType: 'json',
    });
  }
}
