import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';
import Label from 'src/app/domain/Label';

@Component({
  selector: 'app-add-label',
  templateUrl: './add-label.component.html',
  styleUrls: ['./add-label.component.css']
})
export class AddLabelComponent implements OnInit {
  addLabelForm!: FormGroup;

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
    console.log("here's the add groupForm variable ",this.addLabelForm);
    this.addLabelForm = this.fb.group({
      nom: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.addLabelForm.valid) {
      const newGroupe: Label = this.addLabelForm.value;
      this.compagnieService.addLabel(newGroupe).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.addLabelForm.reset();
        this.router.navigate(['/metadata'], { replaceUrl: true, queryParams: { reload: true } });
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addLabelForm.reset();
        this.router.navigate(['/metadata'], { replaceUrl: true, queryParams: { reload: true } });
      }
      );
    }
  }

}
