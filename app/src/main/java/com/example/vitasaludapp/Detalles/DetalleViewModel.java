package com.example.vitasaludapp.Detalles;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitasaludapp.Model.Profesional;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * ViewModel que gestiona el estado de favorito de un profesional sanitario.
 * Permite marcarlo o desmarcarlo como favorito en Firebase Realtime Database.
 */
public class DetalleViewModel extends ViewModel {

    /**
     * LiveData que indica si el profesional actual está marcado como favorito.
     */
    private MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private DatabaseReference favoritosRef;
    private String uid;

    /**
     * Constructor que prepara la referencia a Firebase para el usuario autenticado.
     */
    public DetalleViewModel() {
        favoritosRef = FirebaseDatabase.getInstance().getReference("favoritos");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Retorna LiveData que puede ser observado por la interfaz para saber
     * si el profesional está marcado como favorito.
     *
     * @return LiveData observable del estado favorito.
     */
    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    /**
     * Verifica en Firebase si el profesional está guardado como favorito.
     *
     * @param profesionalNombre nombre del profesional a consultar.
     */
    public void checkFavoriteStatus(String profesionalNombre) {
        favoritosRef.child(uid).orderByChild("nombre").equalTo(profesionalNombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFavorite.setValue(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isFavorite.setValue(false);
                    }
                });
    }

    /**
     * Alterna el estado del profesional: si es favorito, lo elimina; si no, lo añade.
     *
     * @param profesional objeto del profesional sanitario.
     */
    public void toggleFavorite(Profesional.Contenido profesional) {
        if (Boolean.TRUE.equals(isFavorite.getValue())) {
            removeFromFavorites(profesional.getNombre());
        } else {
            addToFavorites(profesional);
        }
    }

    /**
     * Añade el profesional a la lista de favoritos del usuario en Firebase.
     */
    private void addToFavorites(Profesional.Contenido profesional) {
        String favoritoId = favoritosRef.child(uid).push().getKey();
        if (favoritoId != null) {
            HashMap<String, Object> profesionalMap = new HashMap<>();
            profesionalMap.put("nombre", profesional.getNombre());
            profesionalMap.put("direccion", profesional.getDireccion());
            profesionalMap.put("localidad", profesional.getLocalidad());
            profesionalMap.put("precio", profesional.getPrecio());
            profesionalMap.put("telefono", profesional.getTelefono());
            profesionalMap.put("email", profesional.getEmail());
            profesionalMap.put("especialidad", profesional.getEspecialidad());
            profesionalMap.put("horaInicio", profesional.getHoraInicio());
            profesionalMap.put("horaFin", profesional.getHoraFin());
            profesionalMap.put("imagen", profesional.getImagen());
            profesionalMap.put("latitud", profesional.getLatitud());
            profesionalMap.put("longitud", profesional.getLongitud());

            favoritosRef.child(uid).child(favoritoId).setValue(profesionalMap)
                    .addOnSuccessListener(aVoid -> isFavorite.setValue(true));
        }
    }

    /**
     * Elimina el profesional de la lista de favoritos en Firebase.
     */
    private void removeFromFavorites(String profesionalNombre) {
        favoritosRef.child(uid).orderByChild("nombre").equalTo(profesionalNombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> isFavorite.setValue(false));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // No hacemos nada, se mantiene el estado actual
                    }
                });
    }
}