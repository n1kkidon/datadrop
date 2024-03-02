import { Component, Inject } from '@angular/core';
import { FileService } from '../../services/file.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FilepageComponent } from '../filepage/filepage.component';
import { FileInfo } from '../../../shared/models/FileInfo';
import { DirectoryInfo } from '../../../shared/models/DirectoryInfo';
import { UtilsService } from '../../../shared/services/utils.service';
import { Subscription, finalize } from 'rxjs';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-fileupload-dialog',
  templateUrl: './fileupload-dialog.component.html',
  styleUrls: ['./fileupload-dialog.component.scss'],
})
export class FileuploadDialogComponent {
  constructor(
    private fileService: FileService,
    private dialogRef: MatDialogRef<FilepageComponent>,
    @Inject(MAT_DIALOG_DATA) public directoryInfo: DirectoryInfo,
    private utilsService: UtilsService,
  ) {}

  selectedFile: File | null = null;

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onCancelClick() {
    this.dialogRef.close();
  }

  uploadFile(): void {
    if (this.selectedFile) {
      const upload$ = this.fileService
        .uploadFile(
          this.directoryInfo.id,
          this.selectedFile,
          this.directoryInfo.sharedState,
        ) //TODO: can set sharestate when uploading
        .pipe(finalize(this.reset));

      this.uploadSub = upload$.subscribe((event) => {
        if (event.type == HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round(
            100 * (event.loaded / (event.total ? event.total : event.loaded)),
          );
          this.utilsService.fileDownloading.emit(this.uploadProgress);
        } else if (event.type == HttpEventType.Response) {
          if (event.ok) {
            this.utilsService.uploadFinished.emit(event.body!);
            this.utilsService.openSnackBar(
              '"' + event.body?.name + '" successfully uploaded!',
              'OK',
            );
          }
        }
      });
      this.dialogRef.close();
    } else {
      this.utilsService.openSnackBar('No file selected!', 'OK');
    }
  }

  uploadProgress?: number;
  uploadSub?: Subscription;

  private reset() {
    this.uploadSub = undefined;
    this.uploadProgress = undefined;
  }

  cancelUpload() {
    this.uploadSub?.unsubscribe();
    this.reset();
  }
}
