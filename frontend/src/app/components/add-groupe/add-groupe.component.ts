import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';
import { NgToastModule, NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-add-groupe',
  templateUrl: './add-groupe.component.html',
  styleUrls: ['./add-groupe.component.css']
})
export class AddGroupeComponent implements OnInit {
  addGroupeForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private compagnieService: CompagnieService,
    private router:Router,
    private toast: NgToastService
  ) {
    this.createForm();
  }

  ngOnInit(): void {}

  createForm() {
    console.log("here's the add groupForm variable ",this.addGroupeForm);
    this.addGroupeForm = this.fb.group({
      nom: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.addGroupeForm.valid) {
      const newGroupe: Groupe = this.addGroupeForm.value;
      this.compagnieService.createGroup(newGroupe.nom).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.addGroupeForm.reset();
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addGroupeForm.reset();
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
      }
      );
    }
  }
}
