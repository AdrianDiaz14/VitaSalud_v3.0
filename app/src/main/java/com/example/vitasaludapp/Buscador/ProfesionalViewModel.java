package com.example.vitasaludapp.Buscador;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.utils.EspecialidadUtils;
import com.example.vitasaludapp.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel responsable de manejar la lógica de negocio asociada con
 * la búsqueda, filtrado y ordenamiento de profesionales sanitarios.
 * Utiliza LiveData para actualizar la UI reactivamente.
 */
public class ProfesionalViewModel extends ViewModel {

    private MutableLiveData<List<Profesional.Contenido>> profesionales = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMensaje = new MutableLiveData<>();
    private MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    private List<Profesional.Contenido> datosOriginales = new ArrayList<>();

    public enum State {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }

    private MutableLiveData<State> state = new MutableLiveData<>();

    public LiveData<List<Profesional.Contenido>> getProfesionales() {
        return profesionales;
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    public LiveData<String> getErrorMensaje() {
        return errorMensaje;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<State> getState() {
        return state;
    }

    /**
     * Realiza una llamada a la API para obtener todos los profesionales disponibles.
     * Actualiza el estado de carga y el listado.
     */
    public void buscarProfesionales() {
        Log.d("API_CALL", "Buscando todos los profesionales...");
        realizarLlamadaAPI(Profesional.api.obtenerProfesionales());
    }

    /**
     * Realiza la llamada genérica a la API y maneja la respuesta.
     *
     * @param call la llamada Retrofit que se ejecutará.
     */
    private void realizarLlamadaAPI(Call<List<Profesional.Contenido>> call) {
        isLoading.setValue(true);
        state.setValue(State.LOADING);

        call.enqueue(new Callback<List<Profesional.Contenido>>() {
            @Override
            public void onResponse(Call<List<Profesional.Contenido>> call, Response<List<Profesional.Contenido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    datosOriginales = new ArrayList<>(response.body());

                    // Mostrar todos los resultados inicialmente
                    profesionales.setValue(datosOriginales);

                    // Ordenar por distancia si tenemos ubicación
                    if (currentLocation.getValue() != null) {
                        ordenarPorDistancia(currentLocation.getValue());
                    }

                    state.setValue(datosOriginales.isEmpty() ? State.EMPTY : State.SUCCESS);
                } else {
                    profesionales.setValue(null);
                    state.setValue(State.ERROR);
                    errorMensaje.setValue("Error en la respuesta del servidor");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Profesional.Contenido>> call, Throwable t) {
                profesionales.setValue(null);
                isLoading.setValue(false);
                state.setValue(State.ERROR);
                errorMensaje.setValue(t.getMessage());
            }
        });
    }

    /**
     * Filtra la lista de profesionales por nombre, localidad y/o especialidad.
     *
     * @param nombre          nombre parcial del profesional (opcional).
     * @param localidad       localidad (opcional).
     * @param especialidadId  ID de la especialidad (opcional).
     */
    public void filtrarProfesionales(String nombre, String localidad, Long especialidadId) {
        // Si no hay filtros aplicados, mostrar todos los resultados
        if ((nombre == null || nombre.isEmpty()) &&
                (localidad == null || localidad.isEmpty()) &&
                especialidadId == null) {

            profesionales.setValue(datosOriginales);
            if (currentLocation.getValue() != null) {
                ordenarPorDistancia(currentLocation.getValue());
            }
            return;
        }

        List<Profesional.Contenido> filtrados = new ArrayList<>();

        for (Profesional.Contenido profesional : datosOriginales) {
            boolean coincideNombre = nombre.isEmpty() || profesional.getNombre().toLowerCase().contains(nombre.toLowerCase());
            boolean coincideLocalidad = localidad.isEmpty() || profesional.getLocalidad().toLowerCase().contains(localidad.toLowerCase());
            boolean coincideEspecialidad = especialidadId == null ||
                    especialidadId.equals(EspecialidadUtils.getEspecialidadesMap().get(profesional.getEspecialidad()));

            if (coincideNombre && coincideLocalidad && coincideEspecialidad) {
                filtrados.add(profesional);
            }
        }

        // Ordenar por distancia si tenemos ubicación
        if (currentLocation.getValue() != null) {
            Collections.sort(filtrados, (p1, p2) -> calcularDistanciaComparacion(p1, p2, currentLocation.getValue()));
        }

        profesionales.setValue(filtrados);
        state.setValue(filtrados.isEmpty() ? State.EMPTY : State.SUCCESS);
    }

    /**
     * Ordena la lista actual de profesionales por nombre alfabéticamente.
     */
    public void ordenarPorNombre() {
        List<Profesional.Contenido> listaActual = profesionales.getValue();
        if (listaActual != null && !listaActual.isEmpty()) {
            Collections.sort(listaActual, Comparator.comparing(p -> p.nombre));
            profesionales.setValue(listaActual);
        }
    }

    /**
     * Ordena la lista actual de profesionales por precio ascendente.
     */
    public void ordenarPorPrecio() {
        List<Profesional.Contenido> listaActual = profesionales.getValue();
        if (listaActual != null && !listaActual.isEmpty()) {
            Collections.sort(listaActual, Comparator.comparing(p -> p.precio));
            profesionales.setValue(listaActual);
        }
    }

    /**
     * Ordena la lista actual por distancia usando la ubicación actual del usuario.
     *
     * @param location la ubicación actual del usuario.
     */
    public void ordenarPorDistancia(Location location) {
        if (location == null) return;

        List<Profesional.Contenido> listaActual = profesionales.getValue();
        if (listaActual == null || listaActual.isEmpty()) return;

        Collections.sort(listaActual, (p1, p2) -> calcularDistanciaComparacion(p1, p2, location));
        Collections.sort(datosOriginales, (p1, p2) -> calcularDistanciaComparacion(p1, p2, location));

        profesionales.setValue(listaActual);
    }

    private int calcularDistanciaComparacion(Profesional.Contenido p1, Profesional.Contenido p2, Location location) {
        float[] result1 = new float[1];
        float[] result2 = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                p1.getLatitud(), p1.getLongitud(), result1);
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                p2.getLatitud(), p2.getLongitud(), result2);
        return Float.compare(result1[0], result2[0]);
    }

    /**
     * Recupera la última ubicación conocida del usuario y la guarda internamente.
     * Si ya hay profesionales cargados, se ordenan automáticamente por cercanía.
     *
     * @param context el contexto necesario para acceder al proveedor de ubicación.
     */
    public void fetchLocation(Context context) {
        LocationUtils.getLastKnownLocation(context, new LocationUtils.LocationCallback() {
            @Override
            public void onLocationRetrieved(Location location) {
                currentLocation.setValue(location);
                if (profesionales.getValue() != null && !profesionales.getValue().isEmpty()) {
                    ordenarPorDistancia(location);
                }
            }

            @Override
            public void onLocationError(String errorMessage) {
                errorMensaje.setValue(errorMessage);
            }
        });
    }
}