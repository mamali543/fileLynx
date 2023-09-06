import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CollabServiceService } from 'src/app/_services/collab-service.service';
import { FileService } from 'src/app/_services/file.service';
import { HelperService } from 'src/app/_services/helper.service';
import Fichier from 'src/app/domain/Fichier';

@Component({
  selector: 'app-user-search-results',
  templateUrl: './user-search-results.component.html',
  styleUrls: ['./user-search-results.component.css']
})
export class UserSearchResultsComponent implements OnInit{

  labels: string[] = [];
  categorie: string= '';
  query: string = '';
  body: any;
  files: Fichier[] = [];
  showModal= false;
  popupClass= 'popup';

  constructor(private route: ActivatedRoute, private collabService:CollabServiceService,private fileService:FileService,private router:Router,
    private _helper: HelperService) {}


  onFileClick(_t47: Fichier) {
    throw new Error('Method not implemented.');
    }
  ngOnInit(): void {
    //this.router.navigate(['/search'], { queryParams: { labels: this.selectedLabels, categorie: this.selectedCategorie, searchText: this.searchText } });
    // search?labels=2023&labels=Emsi&labels=Minimale&categorie=&searchText=esged
    this.route.queryParams.subscribe(params => {
      this.labels = params['labels'];
      this.categorie = params['categorie'];
      this.query = params['searchText'];
      this.body = {
        "labels": this.labels,
        "category": this.categorie,
        "query": this.query
      }

      this.collabService.getFilteredFiles(this.body).subscribe(
        (data) => {
          this.files = data;
          console.log(data);
        }
      );

    }
    );

  }

  hideModal(): void {
    this.showModal = false;
  }

  openFilePopup(fichier: Fichier) {
    this.router.navigate(['userdashboard/filedetails'], { queryParams: { fileId: fichier.id } });
  }

  deleteFilePopup(fichier: Fichier): void {
    var message = "êtes-vous sûr de vouloir supprimer le fichier "+ fichier.nom+" ?";
    this._helper.show("", message, "", fichier.id, 2 ).then((result) => {
      console.log(result);
    })
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
