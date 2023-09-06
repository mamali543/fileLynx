import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
roles: string[]=[];
  constructor(private router: Router, private tokenStorage: TokenStorageService) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // Check the user's role here
    if (this.tokenStorage.getToken()){

    
    this.roles = this.tokenStorage.getUser().roles; // Replace this with your actual implementation to retrieve the user's role
        console.log("here's the user Role: ", this.roles);
    // Check if the user has the 'ROLE_USER' role
    if (this.roles.includes('ROLE_USER')) {
      // Redirect the user to another component
      console.log('did u get here? ');
      this.router.navigate(['/userdashboard']);
      return false; // Deny access to the current component
    }}
    return true; // Allow access to the current component
  }
}
