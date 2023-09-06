import { Component, OnInit } from '@angular/core';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Log from 'src/app/domain/Log';
import { PageResponse } from 'src/app/domain/PageRespone';


@Component({
  selector: 'app-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.css']
})

export class LogListComponent implements OnInit {

logs: Log[] = [];
filteredLogs: Log[] = [];
searchValue: string = '';
nameOrder: string = '';
page: number = 0;
pageSize: number = 12;
totalLogs!: number;

constructor(private compagnieService: CompagnieService){}
  ngOnInit(): void {
  console.log("hello world");
  this.loadLogs();
}

loadLogs(): void{
  this.compagnieService.getLogsPage(
    this.page,
    this.pageSize,
    'date',
  ).subscribe((response: PageResponse<Log>) => {
    this.logs = response.content;
    console.log("logs: ", this.logs);
    this.filteredLogs = this.logs;
    this.totalLogs = response.totalElements;
  });
}

prevPage(): void {
  if (this.page > 0) {
    this.page--;
    this.loadLogs();
  }
}

nextPage(): void {
  console.log('nextPage');
  if ((this.page + 1) * this.pageSize < this.totalLogs) {
    this.page++;
    this.loadLogs();
  }
}
}
