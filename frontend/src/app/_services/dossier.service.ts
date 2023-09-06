import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Quota from '../domain/Quota';
import { Observable } from 'rxjs/internal/Observable';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};


@Injectable({
  providedIn: 'root'
})
export class DossierService {

  private baseUrl = 'http://'+window.location.hostname+':8080/api/v1/dossier';
  constructor(private http: HttpClient) {}

  deleteFolder(folderId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/admin/delete/${folderId}`, httpOptions);
}


  updateCategorie(catId: number,catName: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateCategorie/${catId}/${catName}`, httpOptions);
  }


}
