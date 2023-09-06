import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../_services/auth.service';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  form: any = {
    username: null,
    email: null,
    password: null,
    confirmPassword: null,
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService, private toast: NgToastService) { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    const { username, email, password, confirmPassword } = this.form;

    if (password !== confirmPassword) {
      this.toast.error({detail:"Message d'erreur", summary:"Verfification de mot de passe echoue", duration:3000});
      return;
    }

    this.authService.register(username, email, password).subscribe(
      data => {
        // console.log("achnahuwa hadchi", data);
      this.toast.success({detail:"Message de réussite", summary:data.message , duration:3000});

        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      (err) => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }
}