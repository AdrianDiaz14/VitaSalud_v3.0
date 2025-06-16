package com.example.vitasaludapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase utilitaria con funciones generales de conversión de fechas y formatos.
 */
public class Utils {

    /**
     * Convierte una combinación de fecha y hora en formato texto a un timestamp en milisegundos.
     *
     * @param fecha fecha en formato "yyyy-MM-dd".
     * @param hora  hora en formato "HH:mm".
     * @return marca de tiempo en milisegundos o 0 si falla el parseo.
     */
    public static long convertirFechaHoraATimestamp(String fecha, String hora) {
        // Cambiado el formato para que coincida con "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(fecha + " " + hora);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Sigue devolviendo 0 si hay un error
        }
    }
}
