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
import Compagnie from '../domain/Compagnie';
import Authorisation from '../domain/Authorisation';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})


export class CompagnieService {

  private baseUrl = 'http://'+window.location.hostname+':8080/api/v1/compagnie';
  constructor(private http: HttpClient) {}


  changeGroupe(username: string, groupe: string):Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/ChangeMemberGroup/${username}/${groupe}`, httpOptions);
  }

  getQuotaStatus():Observable<Quota> {
    return this.http.get<Quota>(this.baseUrl + "/getQuotaStatus", httpOptions);
  }

  deleteCategory(categorieId: number): Observable<any>{
    console.log(categorieId);

    return this.http.delete<any>(`${this.baseUrl}/deleteCategorie/${categorieId}`, httpOptions);
  }

  updateCategorie(catId: number,catName: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateCategorie/${catId}/${catName}`, httpOptions);
  }

  updateCompagnie(catId: number,catName: string, compagnieQuota: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateCompagnieQuota/${catId}/${catName}`, compagnieQuota, httpOptions);
  }

  updateLabel(labelId: number, labelName: string): Observable<any>{
    return this.http.put<any>(`${this.baseUrl}/updateLabel/${labelId}/${labelName}`, httpOptions);

  }

  deleteLabel(labelId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/deleteLabel/${labelId}`, httpOptions);

  }

  getCompagnieLogs(): Observable<Log[]> {
    return this.http.get<Log[]>(this.baseUrl + "/getCompagnieLogs", httpOptions);
  }

  addMembre(user: UserRegister): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/RegisterMembre`, user, httpOptions);
  }

  changeMemberGroup(username: string, groupe: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/ChangeMemberGroup/${username}/${groupe}`, httpOptions);
  }

  createGroup(groupe: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/createGroup/${groupe}`, httpOptions);
  }

  deleteGroupe(groupe: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/deleteGroupe/${groupe}`, httpOptions);
  }

  updateGroupe(groupeId: number,newName: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateGroupe/${groupeId}/${newName}`, httpOptions);
  }

  updateMembre(membre: Membre): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateMembre`, membre, httpOptions);
  }

  deleteMembre(membreId: number, username: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/deleteMembre/${membreId}/${username}`, httpOptions);
  }

  getLogsPage(page: number, size: number, sortBy: string): Observable<PageResponse<Log>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString()).set('sortBy', sortBy.toString());
    return this.http.get<PageResponse<Log>>(
      `${this.baseUrl}/getLogsPagination`,
      { params }
    );
  }

  getGroupesPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string
  ): Observable<PageResponse<Groupe>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    return this.http.get<PageResponse<Groupe>>(
      `${this.baseUrl}/getGroups`,
      { params }
    );
  }

  getAllCompagnies(page: number, size: number, sortBy: string, sortOrder: string,  searchQuery: string): Observable<PageResponse<Compagnie>> {
    let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sortBy', sortBy)
    .set('sortOrder', sortOrder);

  if (searchQuery) {
    params = params.set('searchQuery', searchQuery);
  }

  return this.http.get<PageResponse<Compagnie>>(
    `${this.baseUrl}/getCompagnies`,
    { params }
  );
  }

  getLabelsPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string
  ): Observable<PageResponse<Label>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    return this.http.get<PageResponse<Label>>(
      `${this.baseUrl}/getLabels`,
      { params }
    );
  }

  getCategoriesPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string
  ): Observable<PageResponse<Categorie>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    return this.http.get<PageResponse<Categorie>>(
      `${this.baseUrl}/getCategories`,
      { params }
    );
  }

  getMembresPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string,
    groupFilter: string
  ): Observable<PageResponse<Membre>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    if (groupFilter) {
      params = params.set('groupFilter', groupFilter);
    }

    return this.http.get<PageResponse<Membre>>(
      `${this.baseUrl}/getUsers`,
      { params }
    );
  }

  getAllUniqueGroups(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/distinctGroups`);
  }

  addLabel(label: Label): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/addLabel`, label, httpOptions);
  }

  addCategorie(categorie: Categorie): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/addCategorie`, categorie, httpOptions);
  }

  getMembresByGroup(group: number): Observable<Membre[]> {
    return this.http.get<Membre[]>(`${this.baseUrl}/getMembresByGroupe/${group}`);
  }

  getAllLabels(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/labels`);
  }

  getAllCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/categories`);
  }

  getEntitesCount(): Observable<EntitesCount> {
    return this.http.get<EntitesCount>(`${this.baseUrl}/getEntitiesCount`);
  }
  getQuotaUsedToday(): Observable<QuotaUsedToday> {
    return this.http.get<QuotaUsedToday>(`${this.baseUrl}/getQuotaUsedToday`);
  }
  getTotalAllocatedQuota(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/getTotalAllocatedQuota`);
  }

  getQuotaUsedByDay(): Observable<ConsumptionHistoryChart> {
    return this.http.get<ConsumptionHistoryChart>(`${this.baseUrl}/getQuotaUsedByDay`);
  }

  getAllGroupsConsumption(): Observable<GroupConsumption[]> {
    return this.http.get<GroupConsumption[]>(`${this.baseUrl}/getAllGroupsConsumption`);
  }

  getCompagnieName(): Observable<CompagnieName> {
    return this.http.get<CompagnieName>(`${this.baseUrl}/getCompagnieName`);
  }

  getGroupe(groupeId: number): Observable<Groupe> {
    return this.http.get<Groupe>(`${this.baseUrl}/getGroupe/${groupeId}`);
  }

  getCompagnieUnallocatedQuota(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/getCompagnieUnallocatedQuota`);
  }

  //quota is passed in request body
  updateGroupeQuota(groupeId: number, quota: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateGroupeQuota/${groupeId}`,quota, httpOptions);
  }

  getMembresWithoutAuth(folderId: number): Observable<Membre[]> {
    return this.http.get<Membre[]>(`${this.baseUrl}/getMembresWithoutAuth/${folderId}`);
  }

  getMembresWithAuth(folderId: number): Observable<Membre[]> {
    return this.http.get<Membre[]>(`${this.baseUrl}/getMembresWithAuth/${folderId}`);
  }

  giveMemberAccessToDossier(folderId: number,membreId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/giveMemberAccessToDossier/${folderId}/${membreId}`);
  }

  getAuthObject(folderId: number,resourceAccessorId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/getAuthObject/${folderId}/${resourceAccessorId}`);
  }

  updateAuth(auth:Authorisation): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateAuth`,auth, httpOptions);
  }



}
