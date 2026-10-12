import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CitaService } from '../../services/cita.service';
import { CitaMedica } from '../../models/cita.model';

@Component({
  selector: 'app-cita-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cita-list.component.html',
  styleUrl: './cita-list.component.css'
})
export class CitaListComponent implements OnInit {

  private service = inject(CitaService);

  citas = signal<CitaMedica[]>([]);
  prioridad = signal('TODAS');

  citasFiltradas = computed(() => {
    const filtro = this.prioridad();

    return filtro === 'TODAS'
      ? this.citas()
      : this.citas().filter(c => c.nivelPrioridad === filtro);
  });

  ngOnInit() {
    this.service.getCitas().subscribe(data => {
      this.citas.set(data);
    });
  }

  filtrar(valor: string) {
    this.prioridad.set(valor);
  }

  avanzarEstado(cita: CitaMedica) {
    const estados = ['PENDIENTE', 'EN_ATENCION', 'ATENDIDA'];
    const indice = estados.indexOf(cita.estado);

    if (indice === -1 || indice >= estados.length - 1) return;

    const nuevoEstado = estados[indice + 1];

    this.service.actualizarEstado(cita.id, nuevoEstado).subscribe({
      next: () => {
        this.citas.update(lista =>
          lista.map(c =>
            c.id === cita.id
              ? { ...c, estado: nuevoEstado as CitaMedica['estado'] }
              : c
          )
        );
      }
    });
  }
}