package com.example.vitasaludapp.Favoritos;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.utils.LocationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ViewModel encargado de gestionar los profesionales favoritos del usuario.
 * Carga la información desde Firebase y calcula la distancia a cada profesional en función
 * de la ubicación actual del usuario.
 */
public class FavoritosViewModel extends ViewModel {

    private final MutableLiveData<List<Profesional.Contenido>> favoritosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> ubicacionLiveData = new MutableLiveData<>();

    /**
     * Devuelve una lista observable con los profesionales favoritos actuales.
     */
    public LiveData<List<Profesional.Contenido>> getFavoritos() {
        return favoritosLiveData;
    }

    /**
     * Devuelve errores observables relacionados con la carga de favoritos o ubicación.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Devuelve la ubicación actual del usuario si está disponible.
     */
    public LiveData<Location> getUbicacion() {
        return ubicacionLiveData;
    }

    /**
     * Solicita la ubicación del usuario y ordena los favoritos por distancia si es exitosa.
     *
     * @param context contexto para acceder al proveedor de ubicación.
     */
    public void fetchUserLocation(Context context) {
        LocationUtils.getLastKnownLocation(context, new LocationUtils.LocationCallback() {
            @Override
            public void onLocationRetrieved(Location location) {
                ubicacionLiveData.setValue(location);
                ordenarFavoritosPorDistancia(location);
            }

            @Override
            public void onLocationError(String errorMessage) {
                errorLiveData.setValue("Error obteniendo ubicación: " + errorMessage);
            }
        });
    }

    /**
     * Carga los favoritos del usuario actual desde Firebase y actualiza el LiveData.
     */
    public void cargarFavoritos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favoritosRef = FirebaseDatabase.getInstance().getReference("favoritos").child(uid);

        favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Profesional.Contenido> favoritos = new ArrayList<>();
                for (DataSnapshot favoritoSnapshot : snapshot.getChildren()) {
                    Profesional.Contenido profesional = favoritoSnapshot.getValue(Profesional.Contenido.class);
                    if (profesional != null) {
                        favoritos.add(profesional);
                    }
                }
                favoritosLiveData.setValue(favoritos);

                // Ordenamos si ya tenemos la ubicación
                ordenarFavoritosPorDistancia(ubicacionLiveData.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorLiveData.setValue("Error al cargar favoritos: " + error.getMessage());
            }
        });
    }

    /**
     * Ordena los favoritos cargados por cercanía a la ubicación actual.
     *
     * @param location la ubicación del usuario para comparar distancias.
     */
    private void ordenarFavoritosPorDistancia(Location location) {
        if (location != null && favoritosLiveData.getValue() != null) {
            List<Profesional.Contenido> favoritos = new ArrayList<>(favoritosLiveData.getValue());
            Collections.sort(favoritos, (p1, p2) -> {
                float[] result1 = new float[1];
                float[] result2 = new float[1];

                Location.distanceBetween(location.getLatitude(), location.getLongitude(), p1.getLatitud(), p1.getLongitud(), result1);
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), p2.getLatitud(), p2.getLongitud(), result2);

                return Float.compare(result1[0], result2[0]);
            });

            favoritosLiveData.setValue(favoritos);
        }
    }
}