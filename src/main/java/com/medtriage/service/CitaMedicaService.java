package com.medtriage.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.exception.RecursoNoEncontradoException;
import com.medtriage.model.CitaMedica;
import com.medtriage.model.Doctor;
import com.medtriage.model.Paciente;
import com.medtriage.repository.CitaMedicaRepository;
import com.medtriage.repository.DoctorRepository;
import com.medtriage.repository.PacienteRepository;

@Service
public class CitaMedicaService {

    private final CitaMedicaRepository citaRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    public CitaMedicaService(
            CitaMedicaRepository citaRepository,
            DoctorRepository doctorRepository,
            PacienteRepository pacienteRepository) {

        this.citaRepository = citaRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<CitaMedicaDTO> obtenerTodas() {

        return citaRepository.findAllByOrderByFechaCitaDesc()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaMedicaDTO> obtenerPorPrioridad(String prioridad) {

        if (prioridad == null || prioridad.isBlank()) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria");
        }
        String prioridadNormalizada =
                prioridad.trim().toUpperCase(Locale.ROOT);
        validarPrioridad(prioridadNormalizada);
        return citaRepository
                .findByNivelPrioridad(prioridadNormalizada)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional
    public CitaMedicaDTO crearCita(CrearCitaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos de la cita son obligatorios");
        }

        validarDatosCita(dto);
        Paciente paciente = pacienteRepository
                .findById(dto.getPacienteId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el paciente indicado"));
        Doctor doctor = doctorRepository
                .findById(dto.getDoctorId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el doctor indicado"));
        if (!Boolean.TRUE.equals(doctor.getDisponible())) {
            throw new IllegalStateException(
                    "El doctor seleccionado no está disponible");
        }

        CitaMedica cita = new CitaMedica();
        cita.setCodigoCita(generarCodigoCita());
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setNivelPrioridad(
                dto.getNivelPrioridad()
                        .trim()
                        .toUpperCase(Locale.ROOT));
        cita.setMotivoConsulta(dto.getMotivoConsulta().trim());
        cita.setEstado("PENDIENTE");
        cita.setMontoConsulta(dto.getMontoConsulta());
        cita.setFechaCita(LocalDateTime.now());
        CitaMedica citaGuardada = citaRepository.save(cita);
        return convertirADTO(citaGuardada);
    }

    @Transactional
    public CitaMedicaDTO actualizarEstado(
            Long id,
            String nuevoEstado) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador de la cita es obligatorio");
        }
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nuevo estado es obligatorio");
        }
        String estadoNormalizado =
                nuevoEstado.trim().toUpperCase(Locale.ROOT);
        validarEstado(estadoNormalizado);
        CitaMedica cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe una cita con ID " + id));
        cita.setEstado(estadoNormalizado);
        CitaMedica citaActualizada = citaRepository.save(cita);
        return convertirADTO(citaActualizada);
    }

    private void validarDatosCita(CrearCitaDTO dto) {

        if (dto.getPacienteId() == null) {
            throw new IllegalArgumentException(
                    "El paciente es obligatorio");
        }
        if (dto.getDoctorId() == null) {
            throw new IllegalArgumentException(
                    "El doctor es obligatorio");
        }
        if (dto.getNivelPrioridad() == null
                || dto.getNivelPrioridad().isBlank()) {

            throw new IllegalArgumentException(
                    "La prioridad es obligatoria");
        }
        validarPrioridad(
                dto.getNivelPrioridad()
                        .trim()
                        .toUpperCase(Locale.ROOT));
        if (dto.getMotivoConsulta() == null
                || dto.getMotivoConsulta().isBlank()) {

            throw new IllegalArgumentException(
                    "El motivo de consulta es obligatorio");
        }
        if (dto.getMotivoConsulta().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "El motivo de consulta no puede superar 255 caracteres");
        }
        BigDecimal monto = dto.getMontoConsulta();
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor o igual a cero");
        }
        if (monto.scale() > 2
                || monto.precision() - monto.scale() > 8) {
            throw new IllegalArgumentException(
                    "El monto debe tener como máximo 8 dígitos enteros "
                            + "y 2 decimales");
        }
    }

    private void validarPrioridad(String prioridad) {

        if (!List.of("ALTA", "MEDIA", "BAJA").contains(prioridad)) {
            throw new IllegalArgumentException(
                    "La prioridad debe ser ALTA, MEDIA o BAJA");
        }
    }

    private void validarEstado(String estado) {
        if (!List.of(
                "EN_ATENCION",
                "ATENDIDA",
                "CANCELADA"
        ).contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado debe ser EN_ATENCION, ATENDIDA o CANCELADA");
        }
    }

    private String generarCodigoCita() {
        String codigo;
        do {
            int numero = ThreadLocalRandom.current()
                    .nextInt(1, 10000);
            codigo = String.format(
                    Locale.ROOT,
                    "CIT-2026-%04d",
                    numero);
        } while (citaRepository
                .existsByCodigoCita(codigo));
        return codigo;
    }

    private CitaMedicaDTO convertirADTO(CitaMedica cita) {
        return new CitaMedicaDTO(
                cita.getId(),
                cita.getCodigoCita(),
                cita.getPaciente().getNombreCompleto(),
                cita.getDoctor().getNombre(),
                cita.getNivelPrioridad(),
                cita.getMotivoConsulta(),
                cita.getEstado(),
                cita.getMontoConsulta(),
                cita.getFechaCita()
        );
    }
}