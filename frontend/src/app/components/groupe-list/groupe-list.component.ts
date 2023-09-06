import { ChangeDetectorRef, Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PageResponse } from 'src/app/domain/PageRespone';
import Groupe from 'src/app/domain/Groupe';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-groupe-list',
  templateUrl: './groupe-list.component.html',
  styleUrls: ['./groupe-list.component.css'],
})
export class GroupeListComponent implements OnInit,OnChanges {
  groups: Groupe[] = [];
  filteredGroups: Groupe[] = [];
  searchValue: string = '';
  nameOrder: string = '';
  page: number = 0;
  pageSize: number = 6;
  totalGroups!: number;

  constructor(
    private compagnieService: CompagnieService,
    private route: ActivatedRoute,
    private toast: NgToastService,
    private router: Router,
  ) {}
  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  ngOnInit(): void {
    this.loadGroupes();
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {

          console.log('reload');

          this.loadGroupes();
      }
    });
  }

  tailleToUnit(taille: number, showUnit: boolean = true, unit: string = 'Go', precision:number): string {
    let tailleInUnit = taille;
    if (unit === 'Ko') {
      tailleInUnit /= 1024;
    } else if (unit === 'Mo') {
      tailleInUnit /= (1024 * 1024);
    } else if (unit === 'Go') {
      tailleInUnit /= (1024 * 1024 * 1024);
    } else if (unit === 'To') {
      tailleInUnit /= (1024 * 1024 * 1024 * 1024);
    }
    const formattedTaille = tailleInUnit.toFixed(precision);
    if (showUnit) {
      return `${formattedTaille} ${unit}`;
    } else {
      return formattedTaille;
    }
  }

  loadGroupes(): void {
    console.log("this search value: ", this.searchValue);
    // console.log("this search value: ", this.sortBy);
    this.compagnieService
      .getGroupesPage(
        this.page,
        this.pageSize,
        'nom',
        this.nameOrder,
        this.searchValue
      )
      .subscribe(
        (response: PageResponse<Groupe>) => {
          this.groups = response.content.map((groupe) => {groupe.nom = this.titleCase(groupe.nom); return groupe;});
          this.filteredGroups = this.groups;
          this.totalGroups = response.totalElements;
        },
        (error) => {
          console.error('Error fetching professors:', error);
        }
      );
  }

  onNameOrderChange(order: string): void {
    this.nameOrder = order;
    this.page=0;
    this.loadGroupes();
  }

  onInfoProfessor(groupe: Groupe): void {
    console.log(groupe);
    this.router.navigate(['groups/details/', groupe.id] ,{
      queryParams: { membreData: JSON.stringify(groupe) }
    });
  }
  onSubjectFilterChange(subject: string): void {
    this.page=0;
    this.loadGroupes();
  }

  onSearch(): void {
    this.page=0;
    this.loadGroupes();
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadGroupes();
    }
  }

  nextPage(): void {
    console.log('nextPage');
    if ((this.page + 1) * this.pageSize < this.totalGroups) {
      this.page++;
      this.loadGroupes();
    }
  }

  onDeleteGroupe(groupeNom : string){
    this.compagnieService.deleteGroupe(groupeNom).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadGroupes();
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary: err.error, duration: 3000});
        console.log(err);
      }
    );
  }

  onUpdateGroupe(groupeId : number, newName : string){
    this.compagnieService.updateGroupe(groupeId,newName).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadGroupes();
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary: err.error, duration: 3000});
        console.log(err);
      }
    );
  }

  titleCase(value: string): string {
    if (!value) return value;
    return value.toLowerCase().split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
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
}
