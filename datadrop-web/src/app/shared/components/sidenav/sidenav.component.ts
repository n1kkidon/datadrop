import { firstValueFrom } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../user/services/user.service';
import { UserDto } from '../../models/UserDto';
import { Router } from '@angular/router';
import { SpaceUsageResponse } from '../../models/response/SpaceUsageResponse';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
})
export class SidenavComponent implements OnInit {
  constructor(private userService: UserService) {}

  usersSharingWithMe: UserDto[] = [];
  spaceUsage: SpaceUsageResponse = {
    spaceUsedGb: 0,
    spaceAvailableGb: 0,
    totalSpaceGb: 0,
  };

  async ngOnInit() {
    let response = await firstValueFrom(
      this.userService.getUsersSharingFiles(),
    );
    if (response.ok) {
      this.usersSharingWithMe = response.body!;
    }
    let usageResp = await firstValueFrom(
      this.userService.getSpaceUsageStatsOfCurrentUser(),
    );
    if (usageResp.ok) this.spaceUsage = usageResp.body!;
  }
}
