package com.medtriage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.model.CitaMedica;
import com.medtriage.model.Doctor;
import com.medtriage.model.Paciente;
import com.medtriage.repository.CitaMedicaRepository;
import com.medtriage.repository.DoctorRepository;
import com.medtriage.repository.PacienteRepository;

@ExtendWith(MockitoExtension.class)
public class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private CitaMedicaService citaService;

    @Test
    void testCrearCita() {

        CrearCitaDTO dto = new CrearCitaDTO();
        dto.setPacienteId(1L);
        dto.setDoctorId(1L);
        dto.setNivelPrioridad("ALTA");
        dto.setMotivoConsulta("Fiebre alta");
        dto.setMontoConsulta(new BigDecimal("25000.00"));

        Paciente paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombreCompleto("Elena Madrigal Castro");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setNombre("Dr. Carlos Alvarado");
        doctor.setDisponible(true);

        when(pacienteRepository.findById(1L))
                .thenReturn(Optional.of(paciente));

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(citaRepository.existsByCodigoCita(anyString()))
                .thenReturn(false);

        when(citaRepository.save(any(CitaMedica.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CitaMedicaDTO resultado = citaService.crearCita(dto);

        assertNotNull(resultado);
        assertEquals("CIT-2026-", resultado.getCodigoCita().substring(0, 10));
        assertEquals("ALTA", resultado.getNivelPrioridad());
        assertEquals("Fiebre alta", resultado.getMotivoConsulta());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(citaRepository).save(any(CitaMedica.class));
    }
}