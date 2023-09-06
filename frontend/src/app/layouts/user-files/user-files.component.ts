import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FileService } from 'src/app/_services/file.service';
import { FolderService } from 'src/app/_services/folder.service';
import { HelperService } from 'src/app/_services/helper.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { Location } from '@angular/common';
import Dossier from 'src/app/domain/Dossier';
import Fichier from 'src/app/domain/Fichier';
import { CollabServiceService } from 'src/app/_services/collab-service.service';

@Component({
  selector: 'app-user-files',
  templateUrl: './user-files.component.html',
  styleUrls: ['./user-files.component.css']
})
export class UserFilesComponent implements OnInit{

    currentFolder:Dossier | null = null;
    roles: string[] = [];
    selectedFolder!: Dossier;
    showModal= false;
    popupClass= 'popup';

    constructor(private folderService:FolderService, private tokenStorage: TokenStorageService, private router: Router, private route: ActivatedRoute, private location: Location, private _helper: HelperService,
      private cdRef: ChangeDetectorRef, private fileService:FileService, private collabService:CollabServiceService) {}

    ngOnInit(): void {
      this.loadFolders();
      this.route.queryParams.subscribe(params => {
        if (params['reload']) {
          this.loadFolders();
        }
      });
    }

    loadFolders(): void{

      if(this.currentFolder == null){
        this.collabService.getGroupRootFolder().subscribe(
          (data) => {
            this.currentFolder = data;
            console.log(data);
          },
          (err) => {
            console.log(err);
          }
        );
      }
      else{
        this.onFolderClick(this.currentFolder);
      }
    }

    onFolderClick(folder:Dossier){
      this.collabService.getFolderByIdAsUser(folder.id).subscribe(
        (data) => {
          this.currentFolder = data;
          console.log(data);
        }
      );
    }

    returnToRoot(root:Dossier | undefined){
      this.collabService.getFolderByIdAsUser(root?.id).subscribe(
        (data) => {
          this.currentFolder = data;
          console.log(data);
        }
      );
      }

      OnDeleteIconClique(folder: Dossier) {
        var completeFolder: Dossier;
        this.collabService.getFolderByIdAsUser(folder?.id).subscribe(
          (data) => {
            completeFolder = data;

            console.log(completeFolder);
            this.location.replaceState(`/files/${folder.id}`);
          }
        );
        }

       deletePopup(folder: Dossier): void
      {
        var newFolder: Dossier;
        var f: Dossier;
        var message1 = "êtes-vous sûr de vouloir supprimer le dossier "+ folder.nom+" ?";
        var message2 = "êtes-vous sûr de vouloir supprimer "+ folder.nom+ " ?"+" (Dossier non vide)! "
        this.collabService.getFolderByIdAsUser(folder?.id).subscribe(
          (data) => {
            newFolder = data;
            f=newFolder.racine;
            console.log(f);
            console.log("dossiers: ", newFolder.dossiers.length);
            this._helper.show("", newFolder.dossiers.length > 0 ? message2: message1,"", folder.id, 0).then((result) =>{
              console.log(this.currentFolder)
              if (result == 0)
              {
                  this.collabService.getFolderByIdAsUser(f.id).subscribe((data) =>{
                  this.currentFolder = data;
                })
              }
              // this.cdRef.detectChanges();
            });
          }
        );
      }

      openPopup(folder: Dossier){
        this.router.navigate(['userdashboard/folderdetails'], { queryParams: { folderId: folder.id } });
      }

      hideModal(): void {
        this.showModal = false;
      }

      onFileClick(_t47: Fichier) {
        throw new Error('Method not implemented.');
        }

        deleteFilePopup(fichier: Fichier): void {
          var message = "êtes-vous sûr de vouloir supprimer le fichier "+ fichier.nom+" ?";
          this._helper.show("", message, "", fichier.id, 2 ).then((result) => {
            console.log(result);
            if (result == 0)
            {
                this.folderService.getFolderByIdAsAdmin(fichier.racine.id).subscribe((data) =>{
                this.currentFolder = data;
              })
            }
          })
        }

      openFilePopup(fichier: Fichier) {
        this.router.navigate(['userdashboard/filedetails'], { queryParams: { fileId: fichier.id } });
      }

      onDownload(file: Fichier) {
        this.fileService.downloadFile(file.id).subscribe(
          (response) => {
            const fileName = file.nom; // Get the file name from the Fichier object
            const extension = file.extension; // Get the file extension from the Fichier object
            const blob = new Blob([response.body], { type: response.headers.get('content-type') });
            const url = window.URL.createObjectURL(blob);

            // Create a temporary link element to trigger the download
            const link = document.createElement('a');
            link.href = url;
            link.download = `${fileName}.${extension}`; // Set the file name with extension
            link.click();

            // Clean up the temporary URL
            window.URL.revokeObjectURL(url);
          },
          (err) => {
            console.log(err);
          }
        );
      }
  }



