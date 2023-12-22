import { Component } from '@angular/core';
import { NgxPermissionsService } from 'ngx-permissions';
import { Token } from './models/Token';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent{
  title = 'DataDrop';

  constructor(private permissionsService: NgxPermissionsService){
    let perms = localStorage.getItem('token');
    
    if(!perms){
      this.permissionsService.loadPermissions(["GUEST"]);
    }
    else {
      let token : Token = jwtDecode(perms);
      this.permissionsService.loadPermissions([token.roles[0].authority]);
    }
    console.log(this.permissionsService.getPermissions());
  }
}
