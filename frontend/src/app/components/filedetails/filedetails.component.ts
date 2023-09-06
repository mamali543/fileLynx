import { DOCUMENT } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { data } from 'jquery';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FileService } from 'src/app/_services/file.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Fichier from 'src/app/domain/Fichier';

@Component({
  selector: 'app-filedetails',
  templateUrl: './filedetails.component.html',
  styleUrls: ['./filedetails.component.css']
})
export class FiledetailsComponent {
  labels: string[] = [];
  categories!: string[];
  fileId!: number;
  extension!: string;
  fileName!: string;
  groupe!: string;
  size!: string;
  imageUrl!: any;
  categorie!: string;
  selectedLabels: string[] = [];
  file!: Fichier;


  constructor(private compagnieService: CompagnieService,private route: ActivatedRoute, private fichierService: FileService, private toast: NgToastService,  private router:Router,private tokenStorage: TokenStorageService){}

  ngOnInit(){
    this.extension = '..';
    this.route.params.subscribe(params => {
      this.fileId = params['fileId']; // Here 'id' is the route parameter name defined in the routerLink
      this.fichierService.getFileById(this.fileId).subscribe((data) => {
        this.extension = data.extension;
        this.file = data;
        console.log("data",data);

        this.fileName = data.nom;
        this.selectedLabels = data.labels.map(label => label.nom);
        ;
        this.size = this.tailleToBestUnit(data.taille, true,2) ;
        if (data.categorie)
        this.categorie = data.categorie.nom;
        if(this.isImage()){

          this.fichierService.getImageById(this.fileId).subscribe((response) => {
            const contentType = response.headers.get('Content-Type');
            const blob = new Blob([response.body], { type: contentType });
            console.log(blob);
            // Create a temporary URL for the downloaded image
            this.imageUrl = URL.createObjectURL(blob);

          })
        }
        this.compagnieService.getAllCategories().subscribe((data) => {

          this.categories = data;
          this.categories = this.categories.filter(categorie => categorie !== this.categorie)
        })
        this.compagnieService.getAllLabels().subscribe((data) => {
          this.labels = data;
          this.labels = this.labels.filter(label => !this.selectedLabels.includes(label))
        })

    })
      })



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

onSave() {
  const formData: FormData = new FormData();
        formData.append('selectedLabels', JSON.stringify(this.selectedLabels));
        formData.append('selectedCategorie', this.categorie);
        formData.append('fileName', this.fileName);
        formData.append('fileId', this.fileId.toString());
        console.log(this.selectedLabels);
        console.log(this.categorie);
        console.log(this.fileName);
        let role = ""
        if (this.tokenStorage.getToken())
        role = this.tokenStorage.getUser().roles;
        this.fichierService.update(formData).subscribe((data) => {
          this.router.navigate([role=="ROLE_COMPAGNIE"?'/files':'/userdashboard'],{ replaceUrl: true, queryParams: { reload: true } });
          this.toast.success({detail:"Message de réussite", summary: "Fichier mis a jour avec succès", duration: 3000});
        },
        (err) => {
          this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        }
        );
}

onNamechange(event: Event) {
  const inputElement = event.target as HTMLInputElement;
  const selectedName = inputElement.value;
  console.log(selectedName);
  if (selectedName) {
    this.fileName = selectedName;
    console.log(this.fileName);
  }
  }

onCategorieSelected(event: Event) {
  const selectElement = event.target as HTMLSelectElement;
  const selectedCategorie = selectElement.value;
  console.log(selectedCategorie);
  if (selectedCategorie) {
    this.categorie = selectedCategorie;
    console.log(this.categorie);
  }
  }

isImage(): boolean {
  let imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'ico' , 'tif', 'tiff' , 'jfif' , 'pjpeg' , 'pjp', "avif"];
  return imageExtensions.includes(this.extension);
}

onLabelSelected(event: Event) {
  const selectElement = event.target as HTMLSelectElement;
  const selectedLabel = selectElement.value;

  if (selectedLabel) {
    this.selectedLabels.push(selectedLabel);
    this.labels = this.labels.filter(label => label !== selectedLabel);
    console.log(this.selectedLabels);
    console.log(this.labels);
  }
}

removeLabel(label: string): void
{
  this.selectedLabels = this.selectedLabels.filter(selectedLabel => selectedLabel !== label);
  this.labels.push(label);
}
}
