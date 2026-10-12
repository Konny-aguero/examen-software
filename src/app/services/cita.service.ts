import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CitaMedica, CrearCitaPayload } from '../models/cita.model';

@Injectable({
  providedIn: 'root'
})
export class CitaService {

  private http = inject(HttpClient);
  private api = environment.API_URL + 'citas';

  getCitas() {
    return this.http.get<CitaMedica[]>(this.api);
  }

  getCitasPorPrioridad(prioridad: string) {
    return this.http.get<CitaMedica[]>(
      `${this.api}/prioridad/${prioridad}`
    );
  }

  crearCita(payload: CrearCitaPayload) {
    return this.http.post<CitaMedica>(this.api, payload);
  }

  actualizarEstado(id: number, estado: string) {
    return this.http.patch<CitaMedica>(
      `${this.api}/${id}/estado`,
      { estado }
    );
  }
}