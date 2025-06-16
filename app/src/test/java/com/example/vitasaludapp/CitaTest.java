package com.example.vitasaludapp;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.vitasaludapp.Model.Cita;

public class CitaTest {

    @Test
    public void cita_constructorCompleto() {
        String fecha = "2023-12-25";
        String hora = "15:30";
        String profesional = "Dr. Smith";
        String especialidad = "Cardiología";
        long timestamp = 1671978600000L;

        Cita cita = new Cita(fecha, hora, profesional, especialidad, timestamp);

        assertEquals(fecha, cita.getFecha());
        assertEquals(hora, cita.getHora());
        assertEquals(profesional, cita.getProfesional());
        assertEquals(especialidad, cita.getEspecialidad());
        assertEquals(timestamp, cita.getTimestamp());
    }

    @Test
    public void cita_settersAndGetters() {
        Cita cita = new Cita();
        String fecha = "2023-12-25";
        String hora = "15:30";
        String profesional = "Dr. Smith";
        String especialidad = "Cardiología";
        long timestamp = 1671978600000L;

        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setProfesional(profesional);
        cita.setEspecialidad(especialidad);
        cita.setTimestamp(timestamp);

        assertEquals(fecha, cita.getFecha());
        assertEquals(hora, cita.getHora());
        assertEquals(profesional, cita.getProfesional());
        assertEquals(especialidad, cita.getEspecialidad());
        assertEquals(timestamp, cita.getTimestamp());
    }

    @Test
    public void cita_equalsYHashCode() {
        Cita cita1 = new Cita("2023-12-25", "15:30", "Dr. Smith", "Cardio", 12345);
        Cita cita2 = new Cita("2023-12-25", "15:30", "Dr. Smith", "Cardio", 12345);
        assertEquals(cita1, cita2);
        assertEquals(cita1.hashCode(), cita2.hashCode());
    }

    @Test
    public void cita_notEquals() {
        Cita cita1 = new Cita("2023-12-25", "15:30", "Dr. Smith", "Cardio", 12345);
        Cita cita2 = new Cita("2023-12-26", "15:30", "Dr. Smith", "Cardio", 12345);
        assertNotEquals(cita1, cita2);
    }
}