package com.example.vitasaludapp.Citas;

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

import com.example.vitasaludapp.R;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista de citas ya pasadas para el usuario actual.
 * Usa {@link CitasViewModel} para observar datos desde Firebase y actualizar el RecyclerView.
 */
public class CitasPasadasFragment extends Fragment {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private CitasViewModel citasViewModel;

    /**
     * Infla la vista y configura el RecyclerView y ViewModel asociados.
     *
     * @return la vista raíz del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_pasadas, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_citas_pasadas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        citasAdapter = new CitasAdapter(new ArrayList<>(), false);
        recyclerView.setAdapter(citasAdapter);

        citasViewModel = new ViewModelProvider(requireActivity()).get(CitasViewModel.class);
        setupObservers();

        return view;
    }

    /**
     * Se suscribe a los datos de citas pasadas y errores desde el ViewModel.
     */
    private void setupObservers() {
        citasViewModel.getPastCitas().observe(getViewLifecycleOwner(), citas -> {
            citasAdapter.setCitasList(citas);
        });

        citasViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}