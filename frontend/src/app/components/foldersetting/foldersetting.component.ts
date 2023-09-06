import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgToastComponent, NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FolderService } from 'src/app/_services/folder.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Authorisation from 'src/app/domain/Authorisation';
import Dossier from 'src/app/domain/Dossier';
import Groupe from 'src/app/domain/Groupe';
import Membre from 'src/app/domain/Membre';

@Component({
  selector: 'app-foldersetting',
  templateUrl: './foldersetting.component.html',
  styleUrls: ['./foldersetting.component.css']
})
export class FoldersettingComponent {
  checkboxeUser = [
    { id: 1, description: 'lecture' , checked: false},
    { id: 2, description: 'ecriture', checked: false },
    { id: 3, description: 'modification' , checked: false},
    { id: 4, description: 'suppression', checked: false },
    { id: 5, description: 'telechargement', checked: false },
    { id: 6, description: 'upload', checked: false },
    { id: 7, description: 'creationDossier', checked: false },
    // Add more checkboxes as needed
  ];

  checkboxeGroupe = [
    { id: 1, description: 'lecture' , checked: false},
    { id: 2, description: 'ecriture', checked: false },
    { id: 3, description: 'modification' , checked: false},
    { id: 4, description: 'suppression', checked: false },
    { id: 5, description: 'telechargement', checked: false },
    { id: 6, description: 'upload', checked: false },
    { id: 7, description: 'creationDossier', checked: false },
    // Add more checkboxes as needed
  ];

  membres: Membre[] = [];
  folderId!: number;
  dossier!:Dossier;
  groupe!:Groupe;
  selectedMembre!:number;
  selectedMemberAuth!:Authorisation;
  groupeAuth!:Authorisation;
  roles: string[] = [];
  constructor(private router: Router, private route: ActivatedRoute, private folderService:FolderService,private compagnieService:CompagnieService, private toast: NgToastService, private tokenStorage:TokenStorageService) {}
  ngOnInit() {
    this.roles = this.tokenStorage.getUser().roles;
    this.route.queryParams.subscribe(params => {
      this.folderId = params['folderId'];
      console.log(this.folderId);
      this.folderService.getFolderByIdAsAdmin(this.folderId).subscribe(data => {
        this.dossier = data;
        this.groupe = this.dossier.groupe;
        this.compagnieService.getMembresWithAuth(this.folderId).subscribe(data => {
          this.membres = data;
          if(this.membres.length>0)
          this.selectedMembre= this.membres[0].id;
          console.log(this.membres);
          if(this.membres.length>0)
          this.compagnieService.getAuthObject(this.folderId,this.membres[0].id).subscribe(data => {
            this.selectedMemberAuth = data;
            console.log("auth de user", this.selectedMemberAuth);
            this.checkboxeUser[0].checked = this.selectedMemberAuth.lecture;
            this.checkboxeUser[1].checked = this.selectedMemberAuth.ecriture;
            this.checkboxeUser[2].checked = this.selectedMemberAuth.modification;
            this.checkboxeUser[3].checked = this.selectedMemberAuth.suppression;
            this.checkboxeUser[4].checked = this.selectedMemberAuth.telechargement;
            this.checkboxeUser[5].checked = this.selectedMemberAuth.upload;
            this.checkboxeUser[6].checked = this.selectedMemberAuth.creationDossier;
          });
          this.compagnieService.getAuthObject(this.dossier.id,this.groupe.id).subscribe(data => {
            this.groupeAuth = data;
            console.log("auth de groupe",this.groupeAuth);
            this.checkboxeGroupe[0].checked = this.groupeAuth.lecture;
            this.checkboxeGroupe[1].checked = this.groupeAuth.ecriture;
            this.checkboxeGroupe[2].checked = this.groupeAuth.modification;
            this.checkboxeGroupe[3].checked = this.groupeAuth.suppression;
            this.checkboxeGroupe[4].checked = this.groupeAuth.telechargement;
            this.checkboxeGroupe[5].checked = this.groupeAuth.upload;
            this.checkboxeGroupe[6].checked = this.groupeAuth.creationDossier;

          }
          );
        });
        console.log(this.dossier);
      }
      );
    });

  }

  onUserSelected(event:any){
    this.selectedMembre = event.target.value;
    this.compagnieService.getAuthObject(this.folderId,this.selectedMembre).subscribe(data => {
      this.selectedMemberAuth = data;
      console.log("db user auth ", this.selectedMemberAuth);
      this.checkboxeUser[0].checked = this.selectedMemberAuth.lecture;
      this.checkboxeUser[1].checked = this.selectedMemberAuth.ecriture;
      this.checkboxeUser[2].checked = this.selectedMemberAuth.modification;
      this.checkboxeUser[3].checked = this.selectedMemberAuth.suppression;
      this.checkboxeUser[4].checked = this.selectedMemberAuth.telechargement;
      this.checkboxeUser[5].checked = this.selectedMemberAuth.upload;
      this.checkboxeUser[6].checked = this.selectedMemberAuth.creationDossier;
    });

    console.log(this.selectedMembre);
  }

  openPopup() {

  }

  onUserAuthChange(event:any){
    console.log(event.target.checked);
    this.selectedMemberAuth.lecture = this.checkboxeUser[0].checked;
    this.selectedMemberAuth.ecriture = this.checkboxeUser[1].checked;
    this.selectedMemberAuth.modification = this.checkboxeUser[2].checked;
    this.selectedMemberAuth.suppression = this.checkboxeUser[3].checked;
    this.selectedMemberAuth.telechargement = this.checkboxeUser[4].checked;
    this.selectedMemberAuth.upload = this.checkboxeUser[5].checked;
    this.selectedMemberAuth.creationDossier = this.checkboxeUser[6].checked;
    this.selectedMemberAuth.dossier = this.dossier;
    this.compagnieService.updateAuth(this.selectedMemberAuth).subscribe(data => {
      console.log(data);
      this.toast.success({detail:"Message de réussite", summary: data.message, duration: 500})
    });
  }

  onGroupeAuthChange(event:any){
    console.log(event.target.checked);
    this.groupeAuth.lecture = this.checkboxeGroupe[0].checked;
    this.groupeAuth.ecriture = this.checkboxeGroupe[1].checked;
    this.groupeAuth.modification = this.checkboxeGroupe[2].checked;
    this.groupeAuth.suppression = this.checkboxeGroupe[3].checked;
    this.groupeAuth.telechargement = this.checkboxeGroupe[4].checked;
    this.groupeAuth.upload = this.checkboxeGroupe[5].checked;
    this.groupeAuth.creationDossier = this.checkboxeGroupe[6].checked;
    this.groupeAuth.dossier = this.dossier;
    this.compagnieService.updateAuth(this.groupeAuth).subscribe(data => {
      console.log(data);
      this.toast.success({detail:"Message de réussite", summary: data.message, duration: 500})
    });
  }
}
