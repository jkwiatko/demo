import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {AuthService} from "../../auth/auth.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent implements OnInit {

  constructor(private auth: AuthService, private router: Router, private toastr: ToastrService) {
  }

  ngOnInit(): void {
  }

  submit(form: NgForm) {
    this.auth.login(form.value['email'], form.value['password'])
      .subscribe({
        complete: () => this.router.navigate(['users']), error: err => {
          if (err.status === 401) {
            this.toastr.error("Wrong email or password")
          } else {
            this.toastr.error("Something went wrong try again later")
          }
        }
      })
  }
}
