package com.example.vitasaludapp.Model;

import java.util.Objects;

/**
 * Modelo que representa una cita médica del usuario.
 * Incluye información sobre la fecha, hora, profesional asignado, especialidad
 * y un timestamp que permite ordenar y clasificar cronológicamente.
 */
public class Cita {
    private String fecha;    // Formato "dd/MM/yyyy"
    private String hora;     // Formato "HH:mm"
    private String profesional;   // Nombre del profesional sanitario
    private String especialidad;
    private long timestamp;  // Fecha y hora convertida a milisegundos

    /**
     * Constructor vacío necesario para la deserialización con Firebase.
     */
    public Cita() { }

    /**
     * Constructor completo para crear una cita.
     *
     * @param fecha         Fecha de la cita (yyyy-MM-dd)
     * @param hora          Hora de la cita (HH:mm)
     * @param profesional   Nombre del profesional
     * @param especialidad  Especialidad médica
     * @param timestamp     Marca temporal en milisegundos
     */
    public Cita(String fecha, String hora, String profesional, String especialidad, long timestamp) {
        this.fecha = fecha;
        this.hora = hora;
        this.profesional = profesional;
        this.especialidad = especialidad;
        this.timestamp = timestamp;
    }

    // Getters y setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
    }

    public String getEspecialidad() { return especialidad; }

    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Compara esta cita con otra para verificar si representan la misma información.
     *
     * @param o objeto a comparar
     * @return true si son iguales por contenido lógico
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return timestamp == cita.timestamp &&
                Objects.equals(fecha, cita.fecha) &&
                Objects.equals(hora, cita.hora) &&
                Objects.equals(profesional, cita.profesional) &&
                Objects.equals(especialidad, cita.especialidad);
    }

    /**
     * Genera el código hash consistente con equals.
     */
    @Override
    public int hashCode() {
        return Objects.hash(fecha, hora, profesional, especialidad, timestamp);
    }
}