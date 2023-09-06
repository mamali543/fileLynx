import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';


@Component({
  selector: 'app-user-search-prompt',
  templateUrl: './user-search-prompt.component.html',
  styleUrls: ['./user-search-prompt.component.css']
})
export class UserSearchPromptComponent {

  categories: String[] = [];
  labels: String[] = [];
  selectedLabels: string[] = [];
  selectedCategorie: String= '';
  searchText: string = '';

  constructor(
    private compagnieService: CompagnieService,
    private router:Router,

    ) {}

    ngOnInit() {

      this.compagnieService.getAllLabels().subscribe((data) => {
        this.labels = data;
        console.log(this.labels);
      })

      this.compagnieService.getAllCategories().subscribe((data) => {
        this.categories = data;
        this.selectedCategorie = this.categories[0];
        console.log(this.categories);
      })
      };

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

      search(): void {
        console.log(this.selectedLabels);
        console.log(this.selectedCategorie);
        console.log(this.searchText);

        this.router.navigate(['/search'], { queryParams: { labels: this.selectedLabels, categorie: this.selectedCategorie, searchText: this.searchText } });
      }

}
