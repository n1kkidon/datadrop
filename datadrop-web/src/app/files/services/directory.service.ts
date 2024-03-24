import { DirectoryDto } from '../../shared/models/DirectoryDto';
import { UtilsService } from '../../shared/services/utils.service';
import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ShareStateUpdateRequest } from '../../shared/models/request/ShareStateUpdateRequest';
import { UserDto } from '../../shared/models/UserDto';

@Injectable({
  providedIn: 'root',
})
export class DirectoryService {
  constructor(
    private httpClient: HttpClient,
    private utilsService: UtilsService,
  ) {}

  getDirectoryInfo(
    index: number,
  ): Observable<HttpResponse<DirectoryDto> | HttpErrorResponse> {
    return this.httpClient.get<DirectoryDto>('/directory/' + index + '/info', {
      observe: 'response',
      responseType: 'json',
    });
  }

  createDirectory(
    name: string,
    parentDirectoryId?: number,
    sharedState?: string,
    sharedWithUsers?: number[],
  ) {
    return this.httpClient.post<DirectoryDto>(
      '/directory/create',
      { parentDirectoryId, name, sharedState, sharedWithUsers },
      { observe: 'response', responseType: 'json' },
    );
  }

  renameDirectory(itemId: number, newName: string) {
    return this.httpClient.patch<DirectoryDto>(
      '/directory/rename',
      { itemId, newName },
      { observe: 'response', responseType: 'json' },
    );
  }

  deleteDirectory(directoryId: number) {
    return this.httpClient.delete('/directory/' + directoryId, {
      observe: 'response',
      responseType: 'json',
    });
  }

  shareDirectory(updateRequest: ShareStateUpdateRequest) {
    return this.httpClient.patch('/directory/share', updateRequest, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUsersDirectoryIsSharedWith(directoryId: number) {
    return this.httpClient.get<UserDto[]>('/directory/shared/' + directoryId, {
      observe: 'response',
      responseType: 'json',
    });
  }
}
