import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Dossier from '../domain/Dossier';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class FolderService {

  private baseUrl = 'http://'+window.location.hostname+':8080/api/v1/dossier';
  constructor(private http: HttpClient) {}

  getRootFolderAsAdmin():Observable<Dossier> {
    return this.http.get<Dossier>(this.baseUrl + "/admin/getRoot", httpOptions);
  }

  getFolderByIdAsAdmin(id:number | undefined):Observable<Dossier> {
    return this.http.get<Dossier>(`${this.baseUrl}/admin/get/${id}`, httpOptions);
  }

  addFolderAsAdmin(folder: Dossier, parentId:number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/admin/add/${parentId}`, folder, httpOptions);
  }


}
