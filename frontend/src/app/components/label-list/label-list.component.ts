import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';
import Label from 'src/app/domain/Label';
import { PageResponse } from 'src/app/domain/PageRespone';

@Component({
  selector: 'app-label-list',
  templateUrl: './label-list.component.html',
  styleUrls: ['./label-list.component.css']
})
export class LabelListComponent implements OnInit,OnChanges {
  labels: Label[] = [];
  filteredLabels: Label[] = [];
  searchValue: string = '';
  nameOrder: string = '';
  page: number = 0;
  pageSize: number = 12;
  totalLabels!: number;

  constructor(
    private compagnieService: CompagnieService,
    private route: ActivatedRoute,
    private toast: NgToastService
  ) {}
  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  ngOnInit(): void {
    this.loadLabels();
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {

          console.log('reload');

          this.loadLabels();
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

  loadLabels(): void {
    console.log("this search value: ", this.searchValue);
    // console.log("this search value: ", this.sortBy);
    this.compagnieService
      .getLabelsPage(
        this.page,
        this.pageSize,
        'nom',
        this.nameOrder,
        this.searchValue
      )
      .subscribe(
        (response: PageResponse<Label>) => {
          this.labels = response.content.map((label) => {label.nom = this.titleCase(label.nom); return label;});
          this.filteredLabels = this.labels;
          this.totalLabels = response.totalElements;
        },
        (error) => {
          console.error('Error fetching professors:', error);
        }
      );
  }

  onNameOrderChange(order: string): void {
    this.nameOrder = order;
    this.page=0;
    this.loadLabels();
  }

  onSubjectFilterChange(subject: string): void {
    this.page=0;
    this.loadLabels();
  }

  onSearch(): void {
    this.page=0;
    this.loadLabels();
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadLabels();
    }
  }

  nextPage(): void {
    console.log('nextPage');
    if ((this.page + 1) * this.pageSize < this.totalLabels) {
      this.page++;
      this.loadLabels();
    }
  }

  onDeleteLabel(labelId : number){
    console.log("labelId: ", labelId);
    this.compagnieService.deleteLabel(labelId).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadLabels();
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary: err.error, duration: 3000});
        console.log(err);
      }
    );
  }

  onUpdateLabel(labelId : number, labelName: string){
    this.compagnieService.updateLabel(labelId, labelName).subscribe(
      (data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
        console.log(data);
        this.loadLabels();
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
