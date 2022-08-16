import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EMPTY, Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {UserType} from "./model/user.model";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  public getCurrentUser() : Observable<UserType> {
    return this.http.get<UserType>(environment.api + '/users/me');
  }

  public fetchUsers(): Observable<UserType[]> {
    return this.http.get<UserType[]>(environment.api + '/users');
  }

  public add(email: string, password: string) : Observable<UserType> {
    return this.http.post<UserType>(environment.api + "/users", {email, password})
  }

  public update(id: number, email: string, password: string) : Observable<UserType> {
    return this.http.put<UserType>(environment.api + `/users/${id}`, {email, password});
  }

  public delete(id: number): Observable<never> {
    return this.http.delete(environment.api + `/users/${id}`).pipe(switchMap(() => EMPTY));
  }
}
