import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {AuthService} from "./auth/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'demo';

  loggedIn: any;
  private authSub?: Subscription;

  constructor(private auth: AuthService) {
  }

  public logout(): void {
    this.auth.logout();
  }

  ngOnDestroy(): void {
    this.authSub?.unsubscribe();
  }

  ngOnInit(): void {
    this.auth.isLoginIn.subscribe(isLoggedIn => this.loggedIn = isLoggedIn);
  }
}
