import { FileService } from '../../services/file.service';
import { Component, Inject, OnInit } from '@angular/core';
import { DirectoryService } from '../../services/directory.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { DirectoryDto } from '../../../shared/models/DirectoryDto';
import { FileDto } from '../../../shared/models/FileDto';
import { firstValueFrom } from 'rxjs';
import { ShareStateUpdateRequest } from '../../../shared/models/request/ShareStateUpdateRequest';
import { HttpResponse } from '@angular/common/http';
import { UserDto } from '../../../shared/models/UserDto';

@Component({
  selector: 'app-edit-form-dialog',
  templateUrl: './edit-form-dialog.component.html',
  styleUrls: ['./edit-form-dialog.component.scss'],
})
export class EditFormDialogComponent implements OnInit {
  constructor(
    private directoryService: DirectoryService,
    public dialogRef: MatDialogRef<EditFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { dir: DirectoryDto; editing?: DirectoryDto | FileDto },
    private fileService: FileService,
  ) {}

  directoryForm!: FormGroup;
  isBeingEdited: boolean = !!this.data.editing;
  sharedStates = ['PRIVATE', 'PUBLIC', 'SHARED'];
  isFile = false;

  ngOnInit(): void {
    if (this.data.editing) {
      this.loadFilledFormFile((this.data.editing as FileDto).mimeType != null);
    } else this.loadEmptyForm();
  }

  loadFilledFormFile(isFile: boolean) {
    this.isFile = isFile;
    this.directoryForm = new FormGroup({
      itemName: new FormControl(this.data.editing!.name, [
        Validators.required,
        this.nameExistsValidator1(
          (this.data.dir as DirectoryDto).subdirectories,
        ),
        this.nameExistsValidator((this.data.dir as DirectoryDto).files),
      ]),

      sharedState: new FormControl(
        this.data.editing!.sharedState,
        Validators.required,
      ),
    });
  }

  loadEmptyForm() {
    //leave only 1 method, with default values
    this.directoryForm = new FormGroup({
      itemName: new FormControl('', [
        Validators.required,
        this.nameExistsValidator1(
          (this.data.dir as DirectoryDto).subdirectories,
        ),
        this.nameExistsValidator((this.data.dir as DirectoryDto).files),
      ]),

      sharedState: new FormControl(this.sharedStates[0], Validators.required),
    });
  }

  nameExistsValidator(existingItems: FileDto[]) {
    //leave only 1 method
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value as string;
      let item = existingItems.find((curr) => curr.name === value);

      if (item) {
        if (this.isBeingEdited && item.id == this.data.editing?.id) {
          return null;
        }
        return { nameExists: true };
      }
      // Name is unique, no error
      return null;
    };
  }

  nameExistsValidator1(existingItems: DirectoryDto[]) {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value as string;
      let item = existingItems.find((curr) => curr.name === value);
      if (item) {
        if (this.isBeingEdited && item.id == this.data.editing?.id) {
          return null;
        }
        return { nameExists: true };
      }
      // Name is unique, no error
      return null;
    };
  }

  removeUserFromUpdateRequest(user: UserDto) {
    let index = this.shareUpdReq.shareWithUserIds.indexOf(user.id);
    if (index !== -1) this.shareUpdReq.shareWithUserIds.splice(index, 1); //remove if was temporarily added

    if (!this.shareUpdReq.stopSharingWithUserIds.includes(user.id))
      this.shareUpdReq.stopSharingWithUserIds.push(user.id);
  }

  addUserToUpdateRequest(user: UserDto) {
    this.shareUpdReq.shareWithUserIds.push(user.id);
    console.log(this.shareUpdReq);
  }

  private shareUpdReq: ShareStateUpdateRequest = {
    itemId: 0,
    state: '',
    shareWithUserIds: [],
    stopSharingWithUserIds: [],
  };

  async submitForm() {
    if (this.directoryForm.valid) {
      const formData = this.directoryForm.value;

      if (!this.isBeingEdited) {
        //files shouldnt get here, because you cant create files
        let response = await firstValueFrom(
          this.directoryService.createDirectory(
            formData.itemName,
            this.data.dir.id,
            formData.sharedState,
            this.shareUpdReq.shareWithUserIds,
          ),
        );
        this.onCancelClick(response); //TODO: backend should bet able to take userIds from this
        return;
      }

      this.shareUpdReq.itemId = this.data.editing!.id;
      this.shareUpdReq.state = formData.sharedState;

      if (this.isFile) {
        let response1 = await firstValueFrom(
          this.fileService.renameFile(this.data.editing!.id, formData.itemName),
        );
        let response2 = await firstValueFrom(
          this.fileService.changeFileShareState(this.shareUpdReq),
        );
        if (response1.ok && response2.ok) this.onCancelClick(response1);
      } else {
        let response1 = await firstValueFrom(
          this.directoryService.renameDirectory(
            this.data.editing!.id,
            formData.itemName,
          ),
        );
        let response2 = await firstValueFrom(
          this.directoryService.shareDirectory(this.shareUpdReq),
        );
        if (response1.ok && response2.ok) this.onCancelClick(response1);
      }
    }
  }

  onCancelClick(response?: HttpResponse<any>) {
    if (response && response.ok) {
      this.dialogRef.close(response.body);
      return;
    }
    this.dialogRef.close();
  }
}
