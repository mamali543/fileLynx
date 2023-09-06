import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';

@Component({
  selector: 'app-group-details',
  templateUrl: './group-details.component.html',
  styleUrls: ['./group-details.component.css']
})
export class GroupDetailsComponent implements OnInit {

  constructor(private compagnieService:CompagnieService, private route:ActivatedRoute,private router:Router,
    private toast: NgToastService) { }

  groupe!: Groupe;
  value: number = 0;
  groupeId!: number;
  unallocated!: number;
  taille!: number;
  newQuotaUnit!: number;
  newQuota!: number;
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.groupeId = params['groupId']; // Here 'id' is the route parameter name defined in the routerLink
     this.compagnieService.getGroupe(this.groupeId).subscribe((data) => {
       console.log(data);
       this.groupe = data;
       this.value = parseInt(this.tailleToBestUnit(this.groupe.quota, false, 2))
       this.newQuotaUnit = this.getUnitMultiplier(this.tailleToUnit(this.groupe.quota));
     })
     this.compagnieService.getCompagnieUnallocatedQuota().subscribe((data) => {
      console.log(data);
      this.unallocated = data;
    })

  });


}

handleNewQuotaChange(e: any){
  this.taille = e.target.value;
  this.newQuota = this.taille * this.newQuotaUnit;
  console.log(this.newQuota);

}

handleNewQuotaUnitChange(e: any){
  this.newQuotaUnit = e.target.value;
  this.newQuota = this.taille * this.newQuotaUnit;
  console.log(this.newQuota);
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

  tailleToUnit(taille: number): string {
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
    return unit;
  }

  getUnitMultiplier(unit: string): number {
    switch (unit) {
      case 'To':
        return 1024 * 1024 * 1024 * 1024;
      case 'Go':
        return 1024 * 1024 * 1024;
      case 'Mo':
        return 1024 * 1024;
      case 'Ko':
        return 1024;
      default:
        return 1;
    }
  }

  updateQuota(){
    this.compagnieService.updateGroupeQuota(this.groupe.id, this.newQuota).subscribe((data) => {
      this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
    })
  }

}
