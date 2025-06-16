package com.example.vitasaludapp;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.vitasaludapp.utils.CustomDateValidator;

import java.util.Calendar;

public class CustomDateValidatorTest {

    @Test
    public void isValid_fechaPasada() {
        CustomDateValidator validator = new CustomDateValidator();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Ayer

        assertFalse(validator.isValid(calendar.getTimeInMillis()));
    }

    @Test
    public void isValid_domingo() {
        CustomDateValidator validator = new CustomDateValidator();
        Calendar calendar = Calendar.getInstance();

        // Buscar el próximo domingo
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        assertFalse(validator.isValid(calendar.getTimeInMillis()));
    }

    @Test
    public void isValid_fechaFuturaValida() {
        CustomDateValidator validator = new CustomDateValidator();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2); // Pasado mañana

        // Asegurarnos que no es domingo
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        assertTrue(validator.isValid(calendar.getTimeInMillis()));
    }

    @Test
    public void isValid_hoy() {
        CustomDateValidator validator = new CustomDateValidator();
        assertFalse("La fecha actual debería ser inválida",
                validator.isValid(System.currentTimeMillis()));
    }

    @Test
    public void isValid_limiteMedianoche() {
        CustomDateValidator validator = new CustomDateValidator();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        assertTrue("El primer minuto del día siguiente debería ser válido",
                validator.isValid(calendar.getTimeInMillis()));
    }
}