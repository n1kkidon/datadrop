import { UtilsService } from './../../services/utils.service';
import { DirectoryInfo } from 'src/app/models/DirectoryInfo';
import { FileInfo } from 'src/app/models/FileInfo';
import { UserService } from './../../services/user.service';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserDto } from 'src/app/models/UserDto';
import { DirectoryService } from 'src/app/services/directory.service';
import { FileService } from 'src/app/services/file.service';
import { Observable, debounceTime, distinctUntilChanged, firstValueFrom, of, switchMap } from 'rxjs';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { GuestService } from 'src/app/services/guest.service';

@Component({
  selector: 'app-sharelist',
  templateUrl: './sharelist.component.html',
  styleUrls: ['./sharelist.component.scss']
})
export class SharelistComponent implements OnInit{

  constructor(private directoryService: DirectoryService, private fileService: FileService, private userService: UserService,
    private utilsService: UtilsService, private guestService: GuestService){}

  @Input() itemInfo?: FileInfo | DirectoryInfo;

  users: UserDto[] = [];
  userAddForm!: FormGroup;

  @Output() userAdded = new EventEmitter<UserDto>();
  @Output() userRemoved = new EventEmitter<UserDto>();

  async ngOnInit() {
    this.userAddForm = new FormGroup({
      username: new FormControl('', {
        validators: [Validators.required],
        asyncValidators: [this.checkIfNameExists.bind(this)],
        updateOn: 'change'
      }),
    });
    
    if(!this.itemInfo)
      return;
    if((this.itemInfo as FileInfo).mimeType){
      let resp = await firstValueFrom(this.fileService.getUsersFileIsSharedWith(this.itemInfo.id));
      this.users = resp.ok? resp.body! : [];
    }
    else{
      let resp = await firstValueFrom(this.directoryService.getUsersDirectoryIsSharedWith(this.itemInfo.id));
      this.users = resp.ok? resp.body! : [];
    }
    
  }
  removeUser(user: UserDto){
    this.users.splice(this.users.indexOf(user), 1);
    this.userRemoved.emit(user);
  }

  async submitForm(){
    this.userAddForm.updateValueAndValidity();
    if(this.userAddForm.valid){
      const formData = this.userAddForm.value;
      let user = await firstValueFrom(this.userService.getUserByName(formData.username));
      if(user.ok){
        this.userAdded.emit(user.body!);
        this.users.push(user.body!); //TODO: add validation to not be able to add users that are already added
      }
    }
    else{
      this.utilsService.openSnackBar("Username not found!", "OK");
    }
  }

  checkIfNameExists(control: AbstractControl): Observable<ValidationErrors | null> {
    const name = control.value;
    return this.guestService.checkIfNameAvailable(name).pipe(
      debounceTime(300), // Add a delay to avoid too frequent requests
      distinctUntilChanged(), // Only proceed if the input value has changed
      switchMap(rez => {
        const nameExists = rez.ok && !rez.body!;
        return of(nameExists ? null : { 'nameExists': true });
      })
    );
  }
}
