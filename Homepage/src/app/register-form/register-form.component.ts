import { Component, OnInit } from '@angular/core';
import { GuestService } from '../services/guest.service';
import { Router } from '@angular/router';
import { UtilsService } from '../services/utils.service';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { DirectoryInfo } from '../models/DirectoryInfo';
import { Observable, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent implements OnInit{

  constructor(private guestService: GuestService, private router: Router, private utilsService: UtilsService){}

  hide = true;
  registerForm!: FormGroup;

  changePassVisibility(){
    this.hide = !this.hide;
    this.registerForm.updateValueAndValidity();
  }
  
  ngOnInit(): void {
    const passwordMatchValidator = (control: AbstractControl): { [key: string]: boolean } | null => {
      const password = control.get('pass')?.value;
      const repeatPassword = control.get('repeatPass')?.value;

      // Check if the passwords match
      return (password === repeatPassword || !this.hide) ? null : { 'passwordMismatch': true };
    };

    this.registerForm = new FormGroup({
      username: new FormControl('', {
        validators: [Validators.required],
        asyncValidators: [this.utilsService.checkIfNameAvailable.bind(this)],
        updateOn: 'change' 
      }),
  
      email: new FormControl('', [Validators.required, Validators.email]),
      pass: new FormControl('', [Validators.required, Validators.minLength(7)]), 
      repeatPass: new FormControl('', [this.hide ? Validators.minLength(7) : Validators.nullValidator]),
    }, {validators: passwordMatchValidator});
  }
  
  
  
  onSubmit(){
    this.registerForm.updateValueAndValidity();
    if(this.registerForm.valid){
      const formData = this.registerForm.value;
      this.guestService.register(formData.email, formData.username, formData.pass).subscribe((resp) => {
        if(resp.ok){
          this.utilsService.openSnackBar("Account successfully created!", "OK");
          this.router.navigate(["/login"]);
        }
      });
    }
  }
}
