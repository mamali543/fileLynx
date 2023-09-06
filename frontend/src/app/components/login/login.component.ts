import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../_services/auth.service';
import { TokenStorageService } from '../../_services/token-storage.service';
import { Router } from '@angular/router';
import { NgToastModule, NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService, private router: Router, private toast: NgToastService) { }
  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
      console.log("this caused the routing and the roles are: ", this.roles);
      if (this.roles.includes('ROLE_COMPAGNIE'))
      {
        this.router.navigate(['/dashboard']);
        console.table(this.roles);
      }
      else if (this.roles.includes('ROLE_USER'))
        this.router.navigate(['/userdashboard']);
      else
        this.router.navigate(['/admindashboard']);
    }
  }

  onSubmit(): void {
    const { username, password } = this.form;
    console.log("username and password ", this.form);

    this.authService.login(username, password).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary:"Connexion réussie", duration:2500});
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveRefreshToken(data.refreshToken);
        this.tokenStorage.saveUser(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        setTimeout(() => {
          this.reloadPage();
        }, 2500);
        // this.reloadPage();
      },
     (err) => {
        this.errorMessage = "Invalid Nom d'utilisateur ou Mot de passe";
        // console.log("error message: ", this.errorMessage);
        this.toast.error({detail:"Message d'erreur", summary:this.errorMessage, duration:3000});
        this.isLoginFailed = true;
      }
    );
  }

  reloadPage(): void {
    window.location.reload();
  }
}
