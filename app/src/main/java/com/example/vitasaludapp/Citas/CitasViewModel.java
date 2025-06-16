package com.example.vitasaludapp.Citas;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitasaludapp.Model.Cita;
import com.google.android.gms.tasks.OnCompleteListener;
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
 * ViewModel encargado de gestionar la lógica de citas médicas, tanto futuras como pasadas.
 * Se conecta con Firebase Realtime Database para obtener y actualizar las citas del usuario.
 */
public class CitasViewModel extends ViewModel {

    /**
     * Separa las citas en distintas listas observables para facilitar la representación:
     * todas las citas, pendientes (futuras) y pasadas.
     */
    private final MutableLiveData<List<Cita>> allCitas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Cita>> pendingCitas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Cita>> pastCitas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private DatabaseReference citasRef;
    private ValueEventListener citasListener;

    /**
     * Constructor que configura la referencia al nodo del usuario en Firebase
     * y establece un listener para escuchar cambios en tiempo real.
     */
    public CitasViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        citasRef = FirebaseDatabase.getInstance().getReference("citas").child(uid);
        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        citasListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isLoading.setValue(false);
                List<Cita> citas = new ArrayList<>();
                long now = System.currentTimeMillis();

                for (DataSnapshot citaSnapshot : snapshot.getChildren()) {
                    Cita cita = citaSnapshot.getValue(Cita.class);
                    if (cita != null) {
                        citas.add(cita);
                    }
                }

                // Ordenar todas las citas por timestamp
                Collections.sort(citas, (c1, c2) -> Long.compare(c1.getTimestamp(), c2.getTimestamp()));
                allCitas.setValue(citas);

                // Separar en pendientes y pasadas
                List<Cita> pendientes = new ArrayList<>();
                List<Cita> pasadas = new ArrayList<>();

                for (Cita cita : citas) {
                    if (cita.getTimestamp() > now) {
                        pendientes.add(cita);
                    } else {
                        pasadas.add(cita);
                    }
                }

                pendingCitas.setValue(pendientes);
                pastCitas.setValue(pasadas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading.setValue(false);
                errorMessage.setValue(error.getMessage());
            }
        };
        loadCitas();
    }

    /**
     * Carga las citas del usuario desde Firebase y las clasifica en pendientes y pasadas.
     */
    public void loadCitas() {
        isLoading.setValue(true);
        citasRef.addValueEventListener(citasListener);
    }

    /**
     * Cancela una cita tanto en el nodo de usuario como en el nodo de disponibilidad global.
     *
     * @param cita        la cita a cancelar.
     * @param onComplete  callback que se ejecuta al finalizar el borrado.
     */
    public void cancelCita(Cita cita, OnCompleteListener<Void> onComplete) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas").child(uid);
        DatabaseReference citasOcupadasRef = FirebaseDatabase.getInstance().getReference("citas_ocupadas").child(cita.getFecha());

        citasRef.orderByChild("timestamp").equalTo(cita.getTimestamp())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot citaSnapshot : snapshot.getChildren()) {
                            citaSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        citasOcupadasRef.child(cita.getHora()).removeValue()
                                                .addOnCompleteListener(onComplete);
                                    })
                                    .addOnFailureListener(e ->
                                            errorMessage.setValue("Error al eliminar cita: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        errorMessage.setValue(error.getMessage());
                    }
                });
    }

    /**
     * Limpia los listeners al destruirse el ViewModel para evitar fugas de memoria.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (citasRef != null && citasListener != null) {
            citasRef.removeEventListener(citasListener);
        }
    }

    // Getters públicos para los LiveData observables
    public LiveData<List<Cita>> getAllCitas() { return allCitas; }
    public LiveData<List<Cita>> getPendingCitas() { return pendingCitas; }
    public LiveData<List<Cita>> getPastCitas() { return pastCitas; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}