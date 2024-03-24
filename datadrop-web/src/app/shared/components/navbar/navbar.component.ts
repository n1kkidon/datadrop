import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../user/services/user.service';
import { UtilsService } from '../../services/utils.service';
import { MatDrawer } from '@angular/material/sidenav';
import * as UserActions from '../../../user/actions/user.actions';
import { Store } from '@ngrx/store';
import { AppState } from '../../../app-state';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  constructor(
    private store: Store<AppState>,
    private utilsService: UtilsService,
  ) {}

  @Input() drawer!: MatDrawer;

  uploading = false;
  currUploadProgress = 0;
  currDirectoryName = 'My Storage';

  ngOnInit(): void {
    this.drawer.opened = true;
    this.utilsService.fileDownloading.subscribe((progress) => {
      this.uploading = true;
      this.currUploadProgress = progress;
      if (progress == 100) this.uploading = false;
    });

    this.utilsService.directoryChange.subscribe(
      (dir) => (this.currDirectoryName = dir.name ? dir.name : 'My Storage'),
    );
  }
}
