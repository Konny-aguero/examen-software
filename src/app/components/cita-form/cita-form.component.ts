import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CitaService } from '../../services/cita.service';
import { CrearCitaPayload } from '../../models/cita.model';

@Component({
  selector: 'app-cita-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './cita-form.component.html',
  styleUrl: './cita-form.component.css'
})
export class CitaFormComponent {

  private service = inject(CitaService);
  private router = inject(Router);

  pacienteId = 1;
  doctorId = 1;
  nivelPrioridad: 'ALTA' | 'MEDIA' | 'BAJA' = 'MEDIA';
  motivoConsulta = '';
  montoConsulta = 20000;

  guardar() {
    const payload: CrearCitaPayload = {
      pacienteId: this.pacienteId,
      doctorId: this.doctorId,
      nivelPrioridad: this.nivelPrioridad,
      motivoConsulta: this.motivoConsulta,
      montoConsulta: this.montoConsulta
    };

    this.service.crearCita(payload).subscribe({
      next: () => this.router.navigate(['/citas']),
      error: err => console.error('Error al crear cita:', err)
    });
  }
}