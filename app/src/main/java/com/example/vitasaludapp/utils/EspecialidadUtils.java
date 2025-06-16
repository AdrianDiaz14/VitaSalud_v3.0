package com.example.vitasaludapp.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase utilitaria que contiene la lista de especialidades disponibles en la app.
 * Asocia nombres legibles con identificadores únicos (Long) y los proporciona
 * en forma de lista ordenada para uso en spinners o filtros.
 */
public class EspecialidadUtils {
    private static final Map<String, Long> ESPECIALIDADES_MAP = createEspecialidadesMap();
    private static final String DEFAULT_SPECIALITY = "- Seleccionar especialidad -";

    private static Map<String, Long> createEspecialidadesMap() {
        Map<String, Long> map = new LinkedHashMap<>(); // LinkedHashMap mantiene el orden de inserción
        map.put("Aparato Digestivo", 1L);
        map.put("Cardiologia", 2L);
        map.put("Traumatologia", 3L);
        map.put("Dermatologia", 4L);
        map.put("Geriatria", 5L);
        map.put("Ginecologia y Obstetricia", 6L);
        map.put("Nefrologia", 7L);
        map.put("Neumologia", 8L);
        map.put("Oftalmologia", 9L);
        map.put("Oncologia", 10L);
        map.put("Otorrinolaringologia", 11L);
        map.put("Pediatria", 12L);
        map.put("Psiquiatria", 13L);
        map.put("Rehabilitacion", 14L);
        map.put("Reumatologia", 15L);
        map.put("Urologia", 16L);
        map.put("Podologia", 17L);
        map.put("Fisioterapia", 18L);
        map.put("Nutricion y Dietetica", 19L);
        map.put("Logopedia", 20L);
        map.put("Psicologia", 21L);
        map.put("Sexologia", 22L);
        map.put("Odontologia", 23L);
        map.put("Osteopatia", 24L);
        map.put("Acupuntura", 25L);
        map.put("Homeopatia", 26L);
        map.put("Medicina Estetica", 27L);
        map.put("Farmacologia", 28L);
        map.put("Medicina Paliativa", 29L);
        map.put("Enfermeria General", 30L);
        return Collections.unmodifiableMap(map); // Mapa inmutable
    }

    /**
     * Devuelve un mapa inmutable con todas las especialidades disponibles.
     *
     * @return Mapa con nombre legible como clave y su ID como valor.
     */
    public static Map<String, Long> getEspecialidadesMap() {
        return ESPECIALIDADES_MAP;
    }

    /**
     * Devuelve una lista con los nombres de especialidades sin opción por defecto.
     */
    public static List<String> getEspecialidadesList() {
        return new ArrayList<>(ESPECIALIDADES_MAP.keySet());
    }

    /**
     * Devuelve la lista completa de especialidades incluyendo una opción por defecto en la primera posición.
     */
    public static List<String> getEspecialidadesListWithDefault() {
        List<String> list = getEspecialidadesList();
        list.add(0, DEFAULT_SPECIALITY); // Agrega el ítem por defecto al inicio
        return list;
    }

    /**
     * Devuelve la etiqueta de la opción por defecto.
     */
    public static String getDefaultSpeciality() {
        return DEFAULT_SPECIALITY;
    }
}