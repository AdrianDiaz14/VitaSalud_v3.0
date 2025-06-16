package com.example.vitasaludapp.Citas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitasaludapp.Model.Cita;
import com.example.vitasaludapp.R;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista de citas pendientes del usuario.
 * Permite visualizarlas en una lista y cancelarlas mediante confirmación.
 * Interactúa con {@link CitasViewModel}.
 */
public class CitasPendientesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private CitasViewModel citasViewModel;

    /**
     * Infla la vista del fragmento y configura RecyclerView, ViewModel y listeners.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_pendientes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_citas_pendientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        citasAdapter = new CitasAdapter(new ArrayList<>(), true);
        recyclerView.setAdapter(citasAdapter);

        citasViewModel = new ViewModelProvider(requireActivity()).get(CitasViewModel.class);

        setupObservers();
        setupListeners();

        return view;
    }

    /**
     * Observa los cambios de datos y errores desde el ViewModel.
     */
    private void setupObservers() {
        citasViewModel.getPendingCitas().observe(getViewLifecycleOwner(), citas -> {
            citasAdapter.setCitasList(citas);
        });

        citasViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Mostrar/ocultar progress bar si es necesario
        });

        citasViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configura los listeners, especialmente el de eliminación de citas con confirmación.
     */
    private void setupListeners() {
        citasAdapter.setOnDeleteClickListener(cita -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Anular cita")
                    .setMessage("¿Quiere anular esta cita?")
                    .setPositiveButton("Sí", (dialog, which) ->
                            citasViewModel.cancelCita(cita, task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Cita anulada correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error al anular cita", Toast.LENGTH_SHORT).show();
                                }
                            }))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}