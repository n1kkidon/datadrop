import { LoginResponse } from '../../../shared/models/LoginResponse';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { GuestService } from '../../services/guest.service';
import { Token } from '../../../shared/models/Token';
import { jwtDecode } from 'jwt-decode';
import { RefreshToken } from '../../../shared/models/RefreshToken';
import { NgxPermissionsService } from 'ngx-permissions';
import { Router } from '@angular/router';
import { ErrorResponse } from '../../../shared/models/ErrorResponse';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent {
  constructor(
    private guestService: GuestService,
    private permissionsService: NgxPermissionsService,
    private router: Router,
  ) {}

  hide = true;

  onSubmit(username: string, pass: string) {
    this.guestService.login(username, pass).subscribe((resp) => {
      if (resp.ok) {
        localStorage.setItem('token', resp.body!.token);
        localStorage.setItem('rtoken', resp.body!.refreshToken);
        let token: Token = jwtDecode(resp.body!.token);
        this.permissionsService.loadPermissions([token.roles[0].authority]);
        console.log(this.permissionsService.getPermissions());
        this.router.navigate(['/home']);
      }
    });
  }
}
