export interface Doctor {
  id: number;
  nombre: string;
  especialidad: string;
  disponible: boolean;
}

export interface Paciente {
  id: number;
  identificacion: string;
  nombreCompleto: string;
  correo: string;
  telefono: string;
}

export interface CitaMedica {
  id: number;
  codigoCita: string;
  nombrePaciente: string;
  nombreDoctor: string;
  nivelPrioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  motivoConsulta: string;
  estado: 'PENDIENTE' | 'EN_ATENCION' | 'ATENDIDA' | 'CANCELADA';
  montoConsulta: number;
  fechaCita: string;
}

export interface CrearCitaPayload {
  pacienteId: number;
  doctorId: number;
  nivelPrioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  motivoConsulta: string;
  montoConsulta: number;
}