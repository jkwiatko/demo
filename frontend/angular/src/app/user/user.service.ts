import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {UserType} from "./model/user.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  public fetchUsers(): Observable<UserType[]> {
    return this.http.get<UserType[]>(environment.api + '/users');
  }
}
