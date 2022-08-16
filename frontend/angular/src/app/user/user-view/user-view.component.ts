import {Component, Inject, OnInit} from '@angular/core';
import {UserService} from "../user.service";
import {UserType} from "../model/user.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ToastrService} from "ngx-toastr";
import {switchMap} from "rxjs/operators";

export interface DialogData {
  id: number;
  email: string;
  password: string
}

@Component({
  selector: 'app-users-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.scss']
})
export class UserViewComponent implements OnInit {

  displayedColumns: string[] = ['id', 'email', 'edit', 'delete'];
  dataSource: UserType [] = [];
  currentUser?: UserType;

  constructor(private userService: UserService, public dialog: MatDialog, private toastr: ToastrService) {
  }

  ngOnInit(): void {
    this.userService.fetchUsers().subscribe(users => {
      this.dataSource = users
      this.userService.getCurrentUser().subscribe(user => this.currentUser = user);
    });
  }

  onAdd(email: string, password: string) {
    this.userService.add(email, password)
      .pipe(switchMap(() => this.userService.fetchUsers()))
      .subscribe({next: users => this.dataSource = users, error: this.handleErrors.bind(this)});
  }

  onDelete(id: number): void {
    if (id === this.currentUser?.id) {
      this.toastr.warning("Cannot delete current user due to security reasons")
    } else {
      this.userService.delete(id)
        .subscribe({
          complete: () => this.userService.fetchUsers()
            .subscribe(users => this.dataSource = users),
          error: this.handleErrors.bind(this)
        });
    }
  }

  onEdit(id: number, email: string): void {
    if (id === this.currentUser?.id) {
      this.toastr.warning("Cannot delete current user due to security reasons")
    } else {
      this.openDialog(id, email);
    }
  }

  openDialog(id: number, email: string): void {
    const dialogRef = this.dialog.open(EditUserDialog, {
      width: '350px',
      data: {id, email, password: ""},
    });

    dialogRef.afterClosed().subscribe((result?: DialogData) => {
      if (result) {
        this.userService.update(result.id, result.email, result.password)
          .pipe(switchMap(() => this.userService.fetchUsers()))
          .subscribe({
            next: (users) => {
              this.dataSource = users
            }, error: this.handleErrors.bind(this)
          });
      }
    });
  }

  private handleErrors(error: any) {
    if (typeof error.error === 'string') {
      this.toastr.error(error.error);
    } else {
      this.toastr.error("Something went wrong, please contact the support");
    }
  }
}


@Component({
  selector: 'edit-user-dialog',
  templateUrl: './edit-user-dialog.html',
})
export class EditUserDialog {

  constructor(
    public dialogRef: MatDialogRef<EditUserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
