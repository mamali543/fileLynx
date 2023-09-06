import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';
import Membre from 'src/app/domain/Membre';

@Component({
  selector: 'app-add-folder-collab',
  templateUrl: './add-folder-collab.component.html',
  styleUrls: ['./add-folder-collab.component.css']
})
export class AddFolderCollabComponent implements OnInit {
  addGroupeForm!: FormGroup;
  membersWithoutAuth!: Membre[];
  folderId!: number;
  constructor(
    private fb: FormBuilder,
    private compagnieService: CompagnieService,
    private router:Router,
    private route: ActivatedRoute,
    private toast: NgToastService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.folderId = this.route.snapshot.params['folderId'];
    this.compagnieService.getMembresWithoutAuth(this.folderId).subscribe(
      (data) => {
        this.membersWithoutAuth = data;
        console.log("Members without auths",data);
      },
      (err) => {
        console.log(err);
      }
    );
  }

  createForm() {
    console.log("here's the add groupForm variable ",this.addGroupeForm);
    this.addGroupeForm = this.fb.group({
      id: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.addGroupeForm.valid) {
      const newGroupe: Membre = this.addGroupeForm.value;
      this.compagnieService.giveMemberAccessToDossier(this.folderId,newGroupe.id).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.addGroupeForm.reset();
        this.router.navigate(['/files/folderdetails?folderId='+this.folderId], { replaceUrl: true, queryParams: { reload: true } });
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addGroupeForm.reset();
        this.router.navigate(['/files/folderdetails?folderId='+this.folderId], { replaceUrl: true, queryParams: { reload: true } });
      }
      );
    }
  }
}

