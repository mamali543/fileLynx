import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FolderService } from 'src/app/_services/folder.service';
import * as $ from 'jquery';
import { FileService } from 'src/app/_services/file.service';
import { NgToastService } from 'ng-angular-popup';
import Dossier from 'src/app/domain/Dossier';
import { HttpEventType } from '@angular/common/http';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { CollabServiceService } from 'src/app/_services/collab-service.service';

@Component({
  selector: 'app-filesettings',
  templateUrl: './filesettings.component.html',
  styleUrls: ['./filesettings.component.css']
})
export class FilesettingsComponent {

  categories: String[] = [];
  labels: String[] = [];
  groupe: string = "";
  selectedFile: File | null = null;
  selectedFileName: string = '';
  selectedFileNameWithoutExtension: string = '';
  extension: string = '';
  size: string = '';
  selectedLabels: string[] = [];
  selectedCategorie: string= '';

  folderId!: number;

    constructor(
      private compagnieService: CompagnieService,
      private route: ActivatedRoute,
      private folderService: FolderService,
      private fileService: FileService,
      private toast: NgToastService,
      private router:Router,
      private tokenStorage: TokenStorageService,
      private collabService: CollabServiceService,
      ) {}
  ngOnInit() {
    this.route.params.subscribe(params => {
       this.folderId = params['parentId']; // Here 'id' is the route parameter name defined in the routerLink
      this.collabService.getFolderByIdAsUser(this.folderId).subscribe((data) => {
        console.log(data);
        this.groupe = data.nom;
      })

      // Now you can use the folderId as needed in your component
    });
    this.compagnieService.getAllLabels().subscribe((data) => {
      this.labels = data;
      console.log(this.labels);
    })

    this.compagnieService.getAllCategories().subscribe((data) => {
      this.categories = data;
      this.selectedCategorie = this.categories[0].toString();
      console.log("this is a cat: "+this.selectedCategorie);
    })
      // Use the folderId as needed
    };

    onFileSelected(event: Event) {
      const inputElement = event.target as HTMLInputElement;
      if (inputElement.files && inputElement.files.length > 0) {
        this.selectedFile = inputElement.files[0];
        this.selectedFileName = this.selectedFile.name;
        this.selectedFileNameWithoutExtension = this.getFileDisplayName(this.selectedFile.name);
        this.extension = this.getFileExtension(this.selectedFile.name);
        this.size = this.tailleToBestUnit(this.selectedFile.size, true, 2);

        // Use the selected file here or save it in a variable for later use
        console.log('Selected file:', this.selectedFile);
      }
    }

    onFileNameChanged(event: Event) {
      // Handle changes to the file name input field
      this.selectedFileNameWithoutExtension = (event.target as HTMLInputElement).value;
    }

    getFileDisplayName(fileName: string): string {
      const dotIndex = fileName.lastIndexOf('.');
      return dotIndex !== -1 ? fileName.substring(0, dotIndex) : fileName;
    }

    getFileExtension(fileName: string): string {
      const dotIndex = fileName.lastIndexOf('.');
      return dotIndex !== -1 ? fileName.substring(dotIndex) : '';
    }

    tailleToBestUnit(taille: number, showUnit: boolean = true, precision: number): string {
      let unit = 'Go'; // Start with Gigabytes as the default unit
      let tailleInUnit = taille;

      if (taille >= 1024 * 1024 * 1024 * 1024) {
        unit = 'To';
        tailleInUnit /= (1024 * 1024 * 1024 * 1024);
      } else if (taille >= 1024 * 1024 * 1024) {
        unit = 'Go';
        tailleInUnit /= (1024 * 1024 * 1024);
      } else if (taille >= 1024 * 1024) {
        unit = 'Mo';
        tailleInUnit /= (1024 * 1024);
      } else if (taille >= 1024) {
        unit = 'Ko';
        tailleInUnit /= 1024;
      } else {
        unit = 'B'; // Add Bytes as the smallest unit
      }

      const formattedTaille = tailleInUnit.toFixed(precision);

      if (showUnit) {
        return `${formattedTaille} ${unit}`;
      } else {
        return formattedTaille;
      }
    }

    onLabelSelected(event: Event) {
      const selectElement = event.target as HTMLSelectElement;
      const selectedLabel = selectElement.value;

      console.log(selectedLabel);
      if (selectedLabel) {
        this.selectedLabels.push(selectedLabel);
        this.labels = this.labels.filter(label => label !== selectedLabel);
        console.log(this.selectedLabels);
        console.log(this.labels);
      }
    }

    onUpload() {
      if (this.selectedFile) {
        // If a custom file name is provided, use it; otherwise, use the original file name
        const fileName = this.selectedFileNameWithoutExtension || this.selectedFile.name;
        // Create a new File object with the updated name
        const updatedFile = new File([this.selectedFile], fileName + this.extension);
        const formData: FormData = new FormData();
        formData.append('file', updatedFile);
        formData.append('selectedLabels', JSON.stringify(this.selectedLabels));
        formData.append('selectedCategorie', this.selectedCategorie);
        formData.append('folderId', this.folderId.toString());
        // console.log(this.selectedFile);
        let button = document.getElementById("uploadButton");
        button?.setAttribute("disabled", "true");

        if(button){
          button.innerHTML = "En cour...";
          button.style.backgroundColor = "#505050";
        }

        this.fileService.upload(formData).subscribe((response) => {
          if(response.type === HttpEventType.UploadProgress){
            let percentDone = Math.round(100 * response.loaded / response.total);
            console.log(percentDone);
            if(button){
              button.innerHTML = "En cour... " + percentDone + "%";
            }
          }
          if(response.type === HttpEventType.Response){
            let role = ""
            if (this.tokenStorage.getToken())
            role = this.tokenStorage.getUser().roles;
          console.log(role);

          this.router.navigate([role=="ROLE_COMPAGNIE"?'/files':'/userdashboard'],{ replaceUrl: true, queryParams: { reload: true } });
          this.toast.success({detail:"Message de réussite", summary: "Fichier chargé avec succès", duration: 3000});
          }
        },
        (err) => {
          this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        }
        );
      }
    }

    onCategorieSelected(event: Event) {
      const selectElement = event.target as HTMLSelectElement;
      const selectedCategorie = selectElement.value;

      if (selectedCategorie) {
        this.selectedCategorie = selectedCategorie;
        console.log(this.selectedCategorie);
      }
    }
    removeLabel(label: string): void
      {
        this.selectedLabels = this.selectedLabels.filter(selectedLabel => selectedLabel !== label);
        this.labels.push(label);
      }
  }

