import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { EventEmitter, Injectable, NgZone } from '@angular/core';
import { ErrorResponse } from '../models/ErrorResponse';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  Observable,
  catchError,
  debounceTime,
  distinctUntilChanged,
  mergeMap,
  of,
  retry,
  switchMap,
} from 'rxjs';
import { LoginResponse } from '../models/LoginResponse';
import { Token } from '../models/Token';
import { NgxPermissionsService } from 'ngx-permissions';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { ConfirmDialogComponent } from '../components/confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { DirectoryInfo } from '../models/DirectoryInfo';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { GuestService } from '../../user/services/guest.service';
import { FileInfo } from '../models/FileInfo';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {
  constructor(
    private snackbar: MatSnackBar,
    private httpClient: HttpClient,
    private permissionsService: NgxPermissionsService,
    private router: Router,
    private dialog: MatDialog,
    private guestService: GuestService,
  ) {}

  public directoryChange: EventEmitter<DirectoryInfo> = new EventEmitter();
  public fileDownloading: EventEmitter<number> = new EventEmitter();
  public uploadFinished: EventEmitter<FileInfo> = new EventEmitter();

  public openSnackBar(message: string, action: string) {
    this.snackbar.open(message, action, {
      duration: 4000,
    });
  }
  reLogin(): Observable<HttpResponse<LoginResponse> | HttpErrorResponse> {
    let refreshToken = localStorage.getItem('rtoken');
    return this.httpClient.post<LoginResponse>(
      '/guest/re-login',
      { refreshToken },
      { observe: 'response', responseType: 'json' },
    );
  }

  private clickCount = 0;

  public doubleClick(delegateFunc: () => void): void {
    this.clickCount++;
    if (this.clickCount >= 2) {
      delegateFunc();
      this.clickCount = 0;
    } else if (this.clickCount === 1) {
      setTimeout(() => {
        this.clickCount = 0;
      }, 250);
    }
  }

  public openConfirmActionDialog(dialogText: string) {
    return this.dialog.open(ConfirmDialogComponent, {
      data: dialogText,
      disableClose: false,
      panelClass: 'confirm-dialog',
    });
  }

  checkIfNameAvailable(
    control: AbstractControl,
  ): Observable<ValidationErrors | null> {
    const name = control.value;
    return this.guestService.checkIfNameAvailable(name).pipe(
      debounceTime(300), // Add a delay to avoid too frequent requests
      distinctUntilChanged(), // Only proceed if the input value has changed
      switchMap((rez) => {
        const isUserNameAvailable = rez.ok && rez.body!;
        return of(isUserNameAvailable ? null : { isUserNameAvailable: true });
      }),
    );
  }
}
