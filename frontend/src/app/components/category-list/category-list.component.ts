import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Categorie from 'src/app/domain/Categorie';
import Groupe from 'src/app/domain/Groupe';
import { PageResponse } from 'src/app/domain/PageRespone';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit,OnChanges {
  categories: Categorie[] = [];
  filteredCategories: Categorie[] = [];
  searchValue: string = '';
  nameOrder: string = '';
  page: number = 0;
  pageSize: number = 12;
  totalCategories!: number;

  constructor(
    private compagnieService: CompagnieService,
    private route: ActivatedRoute,
    private toast: NgToastService
  ) {}
  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  ngOnInit(): void {
    this.loadCategories();
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {

          console.log('reload');

          this.loadCategories();
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

  loadCategories(): void {
    console.log("this search value: ", this.searchValue);
    // console.log("this search value: ", this.sortBy);
    this.compagnieService
      .getCategoriesPage(
        this.page,
        this.pageSize,
        'nom',
        this.nameOrder,
        this.searchValue
      )
      .subscribe(
        (response: PageResponse<Categorie>) => {
          this.categories = response.content.map((categorie) => {categorie.nom = this.titleCase(categorie.nom); return categorie;});
          this.filteredCategories = this.categories;
          this.totalCategories = response.totalElements;
          console.log(this.filteredCategories);
        },
        (error) => {
          console.error('Error fetching categries:', error);
        }
      );
  }

  onNameOrderChange(order: string): void {
    this.nameOrder = order;
    this.page=0;
    this.loadCategories();
  }

  onSubjectFilterChange(subject: string): void {
    this.page=0;
    this.loadCategories();
  }

  onSearch(): void {
    this.page=0;
    this.loadCategories();
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadCategories();
    }
  }

  nextPage(): void {
    console.log('nextPage');
    if ((this.page + 1) * this.pageSize < this.totalCategories) {
      this.page++;
      this.loadCategories();
    }
  }

  onDeleteCategory(categorieId: number){
    console.log(categorieId);
    this.compagnieService.deleteCategory(categorieId).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadCategories();
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary: err.error, duration: 3000});
        console.log(err);
      }
    );
  }

  onUpdateCategorie(cat: Categorie){
    console.log("catId: ", cat.id, " cat NewName: ", cat.nom);
    this.compagnieService.updateCategorie(cat.id, cat.nom).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadCategories();
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
}
