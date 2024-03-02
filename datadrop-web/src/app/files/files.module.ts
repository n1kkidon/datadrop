import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FilesRoutingModule } from './files-routing.module';
import { FilesComponent } from './files.component';
import { MaterialModule } from '../shared/modules/material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HomepageComponent } from './containers/homepage/homepage.component';
import { SharelistComponent } from './components/sharelist/sharelist.component';
import { FileuploadDialogComponent } from './components/fileupload-dialog/fileupload-dialog.component';
import { EditFormDialogComponent } from './components/editform-dialog/editform-dialog.component';
import { FilepageComponent } from './components/filepage/filepage.component';
import { NgxPermissionsModule } from 'ngx-permissions';

@NgModule({
  declarations: [
    FilesComponent,
    HomepageComponent,
    SharelistComponent,
    FileuploadDialogComponent,
    EditFormDialogComponent,
    FilepageComponent,
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
