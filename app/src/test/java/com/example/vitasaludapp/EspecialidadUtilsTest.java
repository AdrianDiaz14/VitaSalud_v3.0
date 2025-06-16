package com.example.vitasaludapp;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.vitasaludapp.utils.EspecialidadUtils;

import java.util.List;
import java.util.Map;

public class EspecialidadUtilsTest {

    @Test
    public void getEspecialidadesMap_noVacio() {
        Map<String, Long> especialidades = EspecialidadUtils.getEspecialidadesMap();

        assertNotNull("El mapa no debería ser null", especialidades);
        assertFalse("El mapa no debería estar vacío", especialidades.isEmpty());
    }

    @Test
    public void getEspecialidadesListWithDefault_contieneDefault() {
        List<String> especialidades = EspecialidadUtils.getEspecialidadesListWithDefault();

        assertEquals("El primer elemento debería ser el valor por defecto",
                EspecialidadUtils.getDefaultSpeciality(),
                especialidades.get(0));
    }
}