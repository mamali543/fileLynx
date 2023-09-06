import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import UserRegister from 'src/app/domain/UserRegister';

@Component({
  selector: 'app-add-membre',
  templateUrl: './add-membre.component.html',
  styleUrls: ['./add-membre.component.css']
})
export class AddMembreComponent implements OnInit{
  addMemberForm!: FormGroup;
  groupes!: String[];
  constructor(
    private fb: FormBuilder,
    private compagnieService: CompagnieService,
    private router:Router,
    private toast: NgToastService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.compagnieService.getAllUniqueGroups().subscribe((data) => {
      this.groupes = data;
      console.log("groupes",this.groupes);
    });
  }

  createForm() {
    this.addMemberForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', Validators.required],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      groupe: ['', Validators.required],
      password: ['', Validators.compose([Validators.required, Validators.minLength(6)])]
    });
  }

  get password() {
    return this.addMemberForm.get('password');
  }
  
  onSubmit() {
    if (this.addMemberForm.valid) {
      const newMember: UserRegister = this.addMemberForm.value;
      this.compagnieService.addMembre(newMember).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        console.log('Member added successfully', data);
        this.addMemberForm.reset();
        this.router.navigate(['/users'], { replaceUrl: true, queryParams: {reload: true}});
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addMemberForm.reset();
        this.router.navigate(['/users'], { replaceUrl: true, queryParams: { reload: true } });
      });
    }
  }
}

