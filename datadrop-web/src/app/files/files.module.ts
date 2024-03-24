import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FilesRoutingModule } from './files-routing.module';
import { FilesComponent } from './files.component';
import { MaterialModule } from '../shared/modules/material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FilesListComponent } from './containers/files-list/files-list.component';
import { ShareListComponent } from './components/share-list/share-list.component';
import { FileUploadDialogComponent } from './components/file-upload-dialog/file-upload-dialog.component';
import { EditFormDialogComponent } from './components/edit-form-dialog/edit-form-dialog.component';
import { FilePageComponent } from './components/file-page/file-page.component';
import { NgxPermissionsModule } from 'ngx-permissions';

@NgModule({
  declarations: [
    FilesComponent,
    FilesListComponent,
    ShareListComponent,
    FileUploadDialogComponent,
    EditFormDialogComponent,
    FilePageComponent,
  ],
  imports: [
    CommonModule,
    FilesRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    NgxPermissionsModule,
  ],
})
export class FilesModule {}
