import {Component, OnInit} from '@angular/core';
import {UserService} from "../user.service";
import {UserType} from "../model/user.model";

@Component({
  selector: 'app-users-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.scss']
})
export class UserViewComponent implements OnInit {

  public users: UserType[] = [];

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.userService.fetchUsers().subscribe(users => this.users = users);
  }

}
