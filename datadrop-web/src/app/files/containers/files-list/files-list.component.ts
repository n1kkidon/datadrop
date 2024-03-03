import { DirectoryService } from '../../services/directory.service';
import { Component, OnInit } from '@angular/core';
import { FileService } from '../../services/file.service';
import { DirectoryInfo } from '../../../shared/models/DirectoryInfo';
import { FileInfo } from '../../../shared/models/FileInfo';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { FilepageComponent } from '../../components/filepage/filepage.component';
import { EditFormDialogComponent } from '../../components/editform-dialog/editform-dialog.component';
import { UtilsService } from '../../../shared/services/utils.service';
import { FileuploadDialogComponent } from '../../components/fileupload-dialog/fileupload-dialog.component';
import { AdminService } from '../../../admin/services/admin.service';
import { UserService } from '../../../user/services/user.service';

@Component({
  selector: 'app-files-list',
  templateUrl: './files-list.component.html',
  styleUrls: ['./files-list.component.scss'],
})
export class FilesListComponent implements OnInit {
  constructor(
    private directoryService: DirectoryService,
    private fileService: FileService,
    private activeRoute: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private utilsService: UtilsService,
    private adminService: AdminService,
    private userService: UserService,
  ) {}

  id?: number;

  currentDir: DirectoryInfo = {
    subdirectories: [],
    files: [],
    id: 0,
    name: '',
    creationDate: new Date(),
    lastModifiedDate: new Date(),
    sharedState: '',
    parentDirectoryId: 0,
    owner: {
      id: 0,
      name: '',
      creationDate: new Date(),
      email: '',
      state: '',
    },
    sharedWithUsers: [],
  };

  adminMode = false;
  sharingMode = false;
  userId?: number;

  ngOnInit(): void {
    this.activeRoute.paramMap.subscribe(async (param) => {
      this.adminMode = param.get('userId') != null;
      this.sharingMode = param.get('shareUserId') != null;

      if (param.get('id') != null) {
        this.id = +param.get('id')!;
        this.getCurrentFolder(this.id);
      } else if (this.adminMode) {
        this.userId = +param.get('userId')!;
        this.getRootByUserId(this.userId);
      } else if (this.sharingMode) {
        //admin cant use this currently
        this.userId = +param.get('shareUserId')!;
        this.getSharedFolders(this.userId);
      } else {
        this.getCurrentFolder(0);
      }
    });
    this.utilsService.uploadFinished.subscribe((file) => {
      if (file.parentDirectoryId == this.currentDir.id) {
        this.getCurrentFolder(this.currentDir.id);
      }
    });
  }

  getCurrentFolder(id: number) {
    //TODO: make this recognize what type of fetch it needs (admin, shared, normal)
    this.directoryService.getDirectoryInfo(id).subscribe((response) => {
      if (response.ok) {
        this.currentDir = response.body!;
        this.utilsService.directoryChange.emit(this.currentDir);
      }
    });
  }

  getRootByUserId(id: number) {
    this.adminService.getUserRootDirectoryByUserId(id).subscribe((response) => {
      if (response.ok) {
        this.currentDir = response.body!;
        this.utilsService.directoryChange.emit(this.currentDir);
      }
    });
  }

  getSharedFolders(userId: number) {
    this.userService
      .getDirectoriesSharedWithCurrentUser(userId)
      .subscribe((response) => {
        if (response.ok) {
          this.currentDir.subdirectories = response.body!;
          this.currentDir.name = 'Shared';
          this.utilsService.directoryChange.emit(this.currentDir);
        }
      });
    this.userService
      .getFilesSharedWithCurrentUser(userId)
      .subscribe((response) => {
        if (response.ok) {
          this.currentDir.files = response.body!;
        }
      });
  }

  openFileInfoDialog(selectedFile: FileInfo | DirectoryInfo) {
    this.dialog.open(FilepageComponent, {
      data: selectedFile,
      disableClose: false,
      panelClass: 'filepage-dialog',
    });
  }

  openFileUploadDialog() {
    const uploadDialog = this.dialog.open(FileuploadDialogComponent, {
      data: this.currentDir,
      disableClose: false,
      panelClass: 'fileupload-dialog',
    });
    uploadDialog.afterClosed().subscribe((data: FileInfo) => {
      console.log(data.parentDirectoryId + '==' + this.currentDir.id);
      if (!data || data.parentDirectoryId != this.currentDir.id) return;
      this.getCurrentFolder(this.currentDir.id);
    });
  }

  openEditFormDialog(selectedDir?: DirectoryInfo | FileInfo) {
    const fileDialog = this.dialog.open(EditFormDialogComponent, {
      data: selectedDir
        ? { dir: this.currentDir, editing: selectedDir }
        : { dir: this.currentDir, editing: undefined },
      disableClose: false,
      panelClass: 'editform-dialog',
    });
    fileDialog.afterClosed().subscribe((data: DirectoryInfo | FileInfo) => {
      if (!data) return;
      this.getCurrentFolder(this.currentDir.id);
    });
  }

  deleteFile(file: FileInfo | DirectoryInfo) {
    this.utilsService
      .openConfirmActionDialog(
        'Are you sure you want to delete "' + file.name + '" ?',
      )
      .afterClosed()
      .subscribe((confirmed: boolean) => {
        if (confirmed) {
          if ((file as FileInfo).mimeType) {
            this.fileService
              .deleteFileById(file.id)
              .subscribe((resp) => this.deletionResponse(file.name, resp.ok));
          } else {
            this.directoryService
              .deleteDirectory(file.id)
              .subscribe((resp) => this.deletionResponse(file.name, resp.ok));
          }
        }
      });
  }

  private deletionResponse(name: string, ok: boolean) {
    if (ok) {
      this.utilsService.openSnackBar('Successfully deleted ' + name, 'OK');
      this.getCurrentFolder(this.currentDir.id);
    } else {
      this.utilsService.openSnackBar('Failed to delete ' + name, 'OK');
    }
  }

  doubleClickFileDialog(file: FileInfo) {
    this.utilsService.doubleClick(() => this.openFileInfoDialog(file));
  }

  doubleClickNavigate(id: number): void {
    if (this.adminMode)
      this.utilsService.doubleClick(() =>
        this.router.navigate(['/users/' + this.userId + '/' + id]),
      );
    else if (this.sharingMode)
      this.utilsService.doubleClick(() =>
        this.router.navigate(['/shared/' + this.userId + '/' + id]),
      );
    else
      this.utilsService.doubleClick(() =>
        this.router.navigate(['/home/' + id]),
      );
  }
}
