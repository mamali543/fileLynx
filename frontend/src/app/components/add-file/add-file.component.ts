import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { FolderService } from 'src/app/_services/folder.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Dossier from 'src/app/domain/Dossier';

@Component({
  selector: 'app-add-file',
  templateUrl: './add-file.component.html',
  styleUrls: ['./add-file.component.css']
})
export class AddFileComponent implements OnInit{
  addDossierForm!: FormGroup;
  //I want to put the id here on init
  currentFolder!: number;

  constructor(
    private fb: FormBuilder,
    private folderService: FolderService,
    private router: Router,
    private toast: NgToastService,
    private route: ActivatedRoute,
    private tokenStorage: TokenStorageService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.currentFolder = +params['parentId'];
    });
  }

  createForm() {
    console.log("here's the add groupForm variable ",this.addDossierForm);
    this.addDossierForm = this.fb.group({
      nom: ['', Validators.required],
    });
  }

  onSubmit() {
    let role = ""
            if (this.tokenStorage.getToken())
            role = this.tokenStorage.getUser().roles
    if (this.addDossierForm.valid) {
      const newDossier: Dossier = this.addDossierForm.value;
      this.folderService.addFolderAsAdmin(newDossier,this.currentFolder).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.addDossierForm.reset();
        ;
        this.router.navigate([role=="ROLE_COMPAGNIE"?'/files':'/userdashboard'], { replaceUrl: true, queryParams: { reload: true } });
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addDossierForm.reset();
        this.router.navigate([role=="ROLE_COMPAGNIE"?'/files':'/userdashboard'], { replaceUrl: true, queryParams: { reload: true } });
      }
      );
    }
  }
}
