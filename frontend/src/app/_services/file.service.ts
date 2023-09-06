import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Fichier from '../domain/Fichier';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class FileService {

  private baseUrl = 'http://'+window.location.hostname+':8080/api/v1/fichier';
  constructor(private http: HttpClient) { }


  upload(formData: FormData): Observable<any> {
    const headers = new HttpHeaders();
    headers.delete('Content-Type');
    console.log('uploading file', formData);

    return this.http.post<any>(this.baseUrl + "/upload",formData, { headers , reportProgress: true, observe: 'events'});
  }

  getFileById(fileId: number): Observable<Fichier>
  {
    return this.http.get<Fichier>(`${this.baseUrl}/admin/get/${fileId}`, httpOptions);
  }

  getImageById(fileId: number): Observable<any> {
    const url = `${this.baseUrl}/getImage/${fileId}`;


    return this.http.get<any>(url, {
      responseType: 'blob' as 'json',
      observe: 'response'
    });
  }

  update(formData: FormData): Observable<any> {
    const headers = new HttpHeaders();
    headers.delete('Content-Type');
    console.log('updating file', formData);

    return this.http.post<any>(this.baseUrl + "/updateFile",formData, { headers });
  }

  deleteFile(fileId: number) {
    console.log(fileId);
    return this.http.delete<any>(`${this.baseUrl}/admin/delete/${fileId}`, httpOptions);
  }

  downloadFile(fileId: number): Observable<any> {
    const url = `${this.baseUrl}/downloadFile/${fileId}`;
    return this.http.get<any>(url, {
      responseType: 'blob' as 'json',
      observe: 'response'
    });
  }


}
