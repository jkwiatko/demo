import {Injectable} from '@angular/core';
import {BehaviorSubject, EMPTY, Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {TokenModel} from "./model/token.model";
import {environment} from "../../environments/environment";
import {switchMap, tap, timeout} from "rxjs/operators";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private _isLoginIn = new BehaviorSubject(false);
  private tokenExpirationTimer: ReturnType<typeof setTimeout> | null = null;

  get isLoginIn(): Observable<boolean> {
    return this._isLoginIn.asObservable();
  }

  constructor(private client: HttpClient, private router: Router, private toastr: ToastrService) {
    const token = AuthService.getLocalAccessToken();
    if (token && token.hasNotExpired()) {
      this._isLoginIn.next(true);
      this.addAutoLogout(token.expirationDuration());
    } else {
      this.logout();
    }
  }

  static getLocalAccessToken(): TokenModel | null {
    const json = localStorage.getItem('access_token');

    if (json) {
      const object = JSON.parse(json);
      if (object.token && object.expirationTimestamp) {
        return new TokenModel(object.token, object.expirationTimestamp);
      }
    }

    return null;
  }

  login(email: string, password: string): Observable<never> {
    return this.client.post<TokenModel>(environment.api + '/login', {email, password}).pipe(
      timeout(2000),
      tap(token => this.addAccessToken(new TokenModel(token.token, token.expirationTimestamp))),
      switchMap(() => EMPTY)
    );
  }

  logout() {
    localStorage.removeItem('access_token');
    this._isLoginIn.next(false);
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
      this.router.navigate(['login']);
    }
  }

  addAutoLogout(expirationDuration: number) {
    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
      this.toastr.info("Session has expired, please log in again :)");
    }, expirationDuration);
  }

  addAccessToken(token: TokenModel) {
    localStorage.setItem('access_token', JSON.stringify(token));
    this.addAutoLogout(token.expirationDuration());
    this._isLoginIn.next(true);
  }
}
