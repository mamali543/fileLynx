import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Quota from '../domain/Quota';
import { Observable } from 'rxjs/internal/Observable';
import Log from '../domain/Log';
import UserRegister from '../domain/UserRegister';
import { PageResponse } from '../domain/PageRespone';
import Groupe from '../domain/Groupe';
import Membre from '../domain/Membre';
import Label from '../domain/Label';
import Categorie from '../domain/Categorie';
import { observableToBeFn } from 'rxjs/internal/testing/TestScheduler';
import EntitesCount from '../domain/EntitiesCount';
import QuotaUsedToday from '../domain/QuotaUsedToday';
import ConsumptionHistoryChart from '../domain/ConsumptionHistoryChart';
import GroupConsumption from '../domain/GroupConsumption';
import CompagnieName from '../domain/CompagnieName';
import Authorisation from '../domain/Authorisation';
import Dossier from '../domain/Dossier';
import Fichier from '../domain/Fichier';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class CollabServiceService {

  private baseUrl = 'http://'+window.location.hostname+':8080/api/v1/user';
  constructor(private http: HttpClient) {}

  getGroupRootFolder():Observable<Dossier> {
    return this.http.get<Dossier>(this.baseUrl + "/getRoot", httpOptions);
  }

  getFolderByIdAsUser(id:number | undefined):Observable<Dossier> {
    return this.http.get<Dossier>(`${this.baseUrl}/getDossier/${id}`, httpOptions);
  }
  getFilteredFiles(body:any):Observable<Fichier[]> {
    return this.http.post<Fichier[]>(`${this.baseUrl}/getFilteredFiles`,body, httpOptions);
  }
}
