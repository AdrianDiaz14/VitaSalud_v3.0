package com.example.vitasaludapp.Citas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitasaludapp.Model.Profesional;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel que gestiona el listado de profesionales con citas pendientes.
 * Utilizado por otros fragments para observar el estado de las citas pendientes en tiempo real.
 * <p>
 * Usa LiveData para exponer los datos de forma reactiva a la interfaz de usuario.
 */
public class CitasPendientesViewModel extends ViewModel {

    /**
     * LiveData con la lista de profesionales que tienen citas pendientes.
     * Se expone para observación desde otros fragmentos.
     */
    private MutableLiveData<List<Profesional.Contenido>> citasPendientesListado = new MutableLiveData<>(new ArrayList<>());

    /**
     * Devuelve el LiveData observable con las citas pendientes actuales.
     *
     * @return lista observable de profesionales con citas pendientes.
     */
    public LiveData<List<Profesional.Contenido>> getCitasPendientesItems() {
        return citasPendientesListado;
    }
}