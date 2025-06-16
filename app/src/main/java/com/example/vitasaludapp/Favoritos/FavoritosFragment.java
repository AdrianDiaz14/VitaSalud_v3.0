package com.example.vitasaludapp.Favoritos;

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

import com.example.vitasaludapp.Detalles.DetalleFragment;
import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.R;

/**
 * Fragmento que muestra la lista de profesionales favoritos del usuario.
 * Se suscribe a cambios en los datos del {@link FavoritosViewModel}
 * y permite navegar a la vista detallada de cada profesional.
 */
public class FavoritosFragment extends Fragment {

    private FavoritosViewModel favoritosViewModel;
    private FavoritosAdapter favoritosAdapter;
    private RecyclerView recyclerView;

    /**
     * Infla la vista y configura RecyclerView, ViewModel y listeners.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        setupRecyclerView(view);
        setupViewModel();
        configurarClickItem();

        return view;
    }

    /**
     * Configura el RecyclerView para mostrar los favoritos.
     */
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_favoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritosAdapter = new FavoritosAdapter();
        recyclerView.setAdapter(favoritosAdapter);
    }

    /**
     * Configura las observaciones de favoritos, ubicación y errores desde el ViewModel.
     */
    private void setupViewModel() {
        favoritosViewModel = new ViewModelProvider(this).get(FavoritosViewModel.class);

        // Observamos los favoritos
        favoritosViewModel.getFavoritos().observe(getViewLifecycleOwner(), favoritos -> {
            if (favoritos != null) {
                favoritosAdapter.setProfesionales(favoritos);
            }
        });

        // Observamos la ubicación del usuario
        favoritosViewModel.getUbicacion().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                favoritosAdapter.setCurrentLocation(location, true);
            }
        });

        // Observamos los errores
        favoritosViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Cargamos los favoritos y la ubicación
        favoritosViewModel.cargarFavoritos();
        favoritosViewModel.fetchUserLocation(requireContext());
    }

    /**
     * Configura el evento de clic en un profesional para abrir su vista detallada.
     */
    private void configurarClickItem() {
        favoritosAdapter.setOnItemClickListener(this::abrirDetalleProfesional);
    }

    /**
     * Navega al fragmento de detalles del profesional seleccionado.
     *
     * @param profesional profesional a visualizar.
     */
    private void abrirDetalleProfesional(Profesional.Contenido profesional) {
        Bundle bundle = new Bundle();
        bundle.putString("nombre", profesional.getNombre());
        bundle.putString("imagen", profesional.getImagen());
        bundle.putString("direccion", profesional.getDireccion());
        bundle.putString("localidad", profesional.getLocalidad());
        bundle.putString("telefono", profesional.getTelefono());
        bundle.putString("email", profesional.getEmail());
        bundle.putString("especialidad", profesional.getEspecialidad());
        bundle.putDouble("precio", profesional.getPrecio());
        bundle.putString("horaInicio", profesional.getHoraInicio());
        bundle.putString("horaFin", profesional.getHoraFin());
        bundle.putDouble("latitud", profesional.getLatitud());
        bundle.putDouble("longitud", profesional.getLongitud());

        DetalleFragment detalleFragment = new DetalleFragment();
        detalleFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, detalleFragment)
                .addToBackStack(null)
                .commit();
    }
}