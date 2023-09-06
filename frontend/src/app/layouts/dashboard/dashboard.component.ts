import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import EntitesCount from 'src/app/domain/EntitiesCount';
import Log from 'src/app/domain/Log';
import Quota from 'src/app/domain/Quota';
import QuotaUsedToday from 'src/app/domain/QuotaUsedToday';
import Chart from 'chart.js/auto';
import ConsumptionHistoryChart from 'src/app/domain/ConsumptionHistoryChart';
import GroupConsumption from 'src/app/domain/GroupConsumption';
import CompagnieName from 'src/app/domain/CompagnieName';
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  quota!: Quota;
  logs!: Log[];
  selectedLog!: Log;
  showModal = false; // Flag to control the visibility of the modal
  popupClass = 'popup';
  popupBackgroundColor = '';
  roles: string[] = [];
  entitiesCount!:EntitesCount;
  quotaUsedToday!:QuotaUsedToday;
  totalAllocatedQuota!:number;
  consumptionHistoryChart: any = []
  GroupeAllocationChart: any = []
  consumptionHistory!:ConsumptionHistoryChart;
  groupConsumption!:GroupConsumption[]
  compagnieName!:CompagnieName;
  constructor(private compagnieService: CompagnieService, private tokenStorage: TokenStorageService, private router: Router) {}

  generateWarmColors(numColors: number) {
    const colors = [];
    const warmColorRange = {
      red: [227, 255],
      green: [80, 200],
      blue: [35, 120],
    };
    const gray = `rgba(200, 200, 200)`;
    colors.push(gray);
    for (let i = 0; i < numColors; i++) {
      const red = this.getRandomNumber(warmColorRange.red[0], warmColorRange.red[1]);
      const green = this.getRandomNumber(warmColorRange.green[0], warmColorRange.green[1]);
      const blue = this.getRandomNumber(warmColorRange.blue[0], warmColorRange.blue[1]);
      const alpha = 1;
      const rgbaColor = `rgba(${red},${green},${blue},${alpha})`;
      colors.push(rgbaColor);
    }

    return colors;
  }

  getRandomNumber(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }
  ngOnInit(): void {
    this.compagnieService.getCompagnieName().subscribe(
      (data) => {
        console.log("compagnie name",data);

        this.compagnieName = data;
      }
    );


    this.compagnieService.getQuotaStatus().subscribe(
      (data) => {
        console.log("quota",data);

        this.quota = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getQuotaUsedByDay().subscribe(
      (data) => {
        console.log("consumption history",data);
        this.consumptionHistory = data;
        var cumullativeData:number[] = [];
        data.consumptionHistory.reduce(function(a,b,i) { return cumullativeData[i] = a+b; },0);

       console.log(cumullativeData);
        this.consumptionHistoryChart = new Chart('consumptionHistoryChart', {
          type: 'line',
          data: {
            labels: this.consumptionHistory.labels.map((label) => this.formatDateToDDMM(label)),
            datasets: [
              {
                label: 'Comsomation/jour',
                data: this.consumptionHistory.consumptionHistory,
                borderWidth: 3,
                fill:true,
                borderColor: 'rgba(255, 132, 0, 1)',
                backgroundColor: 'rgba(255, 132, 0, 1)',
                tension: 0.3,
              },
              {
                label: 'Comsomation totale',
                data: cumullativeData,
                borderWidth: 3,
                fill:true,
                borderColor: 'rgba(247,181,55, 1)',
                backgroundColor: 'rgba(247,181,55, 1)',
                tension: 0.3,
              },
            ],
          },
          options: {
            plugins: {
              tooltip: {
                callbacks: {
                  label: function(context) {
                    let label = context.dataset.label || '';
                    var t = (taille: number, showUnit: boolean = true, precision: number): string => {
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
                    return t(context.parsed.y, true, 2);
                }
              }
              }

            },
            responsive: true, // Enable responsiveness
            maintainAspectRatio: false, // Disable aspect ratio maintenance
            scales: {

              y: {
                beginAtZero: true,
                ticks: {
                  callback: function(value, index, values) {
                    var t = (taille: number, showUnit: boolean = true, precision: number): string => {
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
                    return t(parseInt(value.toString()), true, 2);
                  }

              },
              },
            },
          },
        });
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getAllGroupsConsumption().subscribe(
      (data) => {
        console.log("group consumption",data);
        this.groupConsumption = data;
        let labels:string[] = data.map((group) => group.name);
        let dataValues:number[] = data.map((group) => group.consumption);

        function swap(arra: any[]) { [arra[0], arra[arra. length - 1]] = [arra[arra. length - 1], arra[0]]; return arra; }
        dataValues = swap(dataValues);
        labels = swap(labels);
        this.GroupeAllocationChart = new Chart('GroupeAllocationChart', {
          type: 'pie',
          data: {
            labels: labels,
            datasets: [{
              label: 'My First Dataset',
              data: dataValues,
              hoverOffset: 4,
              backgroundColor: this.generateWarmColors(data.length) ,
            }]
          },
          options: {
            elements: {
              arc: {
                  borderWidth: 0
              }
          },
            responsive: true, // Enable responsiveness
            maintainAspectRatio: false, // Disable aspect ratio maintenance
            plugins: {


              tooltip: {
                callbacks: {

                  label: function(context) {
                    let label = context.dataset.label || '';
                    var t = (taille: number, showUnit: boolean = true, precision: number): string => {
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
                    return t(context.parsed, true, 2);
                }
              }
              }

          },
          }

        });
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getTotalAllocatedQuota().subscribe(
      (data) => {
        console.log("total allocated quota",data);
        this.totalAllocatedQuota = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getQuotaUsedToday().subscribe(
      (data) => {
        console.log("quota used today",data);
        this.quotaUsedToday = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getCompagnieLogs().subscribe(
      (data) => {
        //get the 5 last logs
        this.logs = data.slice(Math.max(data.length - 4, 0));
        console.log(data);
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getEntitesCount().subscribe(
      (data) => {
        this.entitiesCount = data;
      }
    );

    //charts
 const data = [26549 ,36549, 34549, 32549, 34345, 19345]
 // generate another array that has cumulative numbers for the data, each value is the sum of all previous values + the current one





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

  openPopup(log: Log): void
  {
      this.selectedLog = log;
      console.log(this.selectedLog);
      this.showModal = true; // Open the modal

      this.popupClass = 'popup open-popup'; // Add or remove CSS class as needed

    }

  hideModal(): void {
    this.showModal = false;
  }

  getLogType(logType: string): string
  {
    switch(logType)
    {
      case 'CRÉER':
        return 'blue-background';
      case 'MODIFIER':
        return 'green-background';
      case 'SUPPRIMER':
          return 'red-background';
      default:
        return logType;
    }
  }

  formatDateToDDMM(date:string) {
    //current format YYYY-MM-DDT00:00:00.000+00:00
    //expected format DD/MM
    return date.substring(8,10) + '/' + date.substring(5,7);


  }

  getLogTypeColor(logType: string): string
  {
    switch(logType)
    {
      case 'CRÉER':
        return '#C9E2F5';
      case 'MODIFIER':
        return '#EDE6AA';
      case 'SUPPRIMER':
          return '#EB7676';
      default:
        return 'logType';
    }
  }


}
