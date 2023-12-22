import { Component, Input, OnInit } from '@angular/core';
import { HomepageComponent } from '../homepage/homepage.component';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { UtilsService } from '../services/utils.service';
import { MatDrawer } from '@angular/material/sidenav';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit{
  
  constructor(private userService: UserService, private router: Router,
     private utilsService: UtilsService){}

  @Input() drawer!: MatDrawer;
  
  uploading = false;
  currUploadProgress = 0;
  currDirectoryName = "Home";

  ngOnInit(): void {
    this.utilsService.fileDownloading.subscribe(progress=> {
      this.uploading = true;
      this.currUploadProgress = progress;
      if(progress == 100)
        this.uploading = false;
    });

    this.utilsService.directoryChange.subscribe(dir => this.currDirectoryName = dir.name ? dir.name : "Home");
  }

  logout(){
    this.userService.logout();
    this.router.navigate(['/login']);
  }
}
