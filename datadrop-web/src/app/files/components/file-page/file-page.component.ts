import { firstValueFrom } from 'rxjs';
import { Component, Inject, OnInit, SecurityContext } from '@angular/core';
import { FileDto } from '../../../shared/models/FileDto';
import { ActivatedRoute, Router } from '@angular/router';
import { FileService } from '../../services/file.service';
import { HttpResponse } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DirectoryDto } from '../../../shared/models/DirectoryDto';

@Component({
  selector: 'app-file-page',
  templateUrl: './file-page.component.html',
  styleUrls: ['./file-page.component.scss'],
})
export class FilePageComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private fileService: FileService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private dialogRef: MatDialogRef<FilePageComponent>,
    @Inject(MAT_DIALOG_DATA) public fileInfo: FileDto | DirectoryDto,
  ) {}

  isFile = !!(this.fileInfo as FileDto).mimeType;
  imgurl!: string | ArrayBuffer | null;
  fileType!: string;
  sharedUsers!: string[];

  async ngOnInit() {
    let sharedUsers = await firstValueFrom(
      this.fileService.getUsersFileIsSharedWith(this.fileInfo.id),
    );
    if (sharedUsers.ok) this.sharedUsers = sharedUsers.body!.map((x) => x.name);
  }

  downloadFile(saveToPc: boolean) {
    if (!this.isFile)
      //folder download is not supported yet (once you can zip stuff)
      return;
    this.fileService.downloadFile(this.fileInfo.id).subscribe((file) => {
      if (file.ok) {
        let blob = new Blob([file.body!], {
          type: (this.fileInfo as FileDto).mimeType,
        });
        if (saveToPc) {
          let fileName = this.getFileNameFromHeader(file);
          saveAs(blob, fileName);
        } else {
          this.openInNewTab(blob);
        }
      }
    });
  }

  getFileNameFromHeader(file: HttpResponse<Blob>) {
    const contentDispositionHeader = file.headers.get('content-disposition');
    return contentDispositionHeader
      ? contentDispositionHeader.split(';')[1].trim().split('=')[1]
      : 'downloaded-file';
  }

  openInNewTab(blob: Blob) {
    const blobUrl: SafeUrl = this.sanitizer.bypassSecurityTrustUrl(
      URL.createObjectURL(blob),
    );
    var url = this.sanitizer.sanitize(SecurityContext.URL, blobUrl);
    window.open(url!);
  }

  checkMimeType() {
    return this.allowedMimeTypes.includes((this.fileInfo as FileDto).mimeType);
  }

  closeDialog() {
    this.dialogRef.close();
  }

  allowedMimeTypes = [
    'text/html',
    'text/plain',
    'text/css',
    'text/javascript',
    'text/xml',
    'application/json',
    'application/xml',
    'application/javascript',
    'application/xhtml+xml',
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp',
    'image/svg+xml',
    'image/bmp',
    'image/tiff',
    'image/x-icon',
    'audio/mpeg',
    'audio/wav',
    'video/mp4',
    'video/webm',
    'application/pdf',
  ];
}
