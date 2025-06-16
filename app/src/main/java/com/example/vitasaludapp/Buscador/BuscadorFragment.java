package com.example.vitasaludapp.Buscador;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitasaludapp.Citas.CitasPendientesViewModel;
import com.example.vitasaludapp.Detalles.DetalleFragment;
import com.example.vitasaludapp.R;
import com.example.vitasaludapp.utils.EspecialidadUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fragmento responsable de mostrar la interfaz de búsqueda de profesionales sanitarios.
 * Permite filtrar por nombre, localidad y especialidad, y mostrar los resultados en un RecyclerView.
 * También gestiona el ordenamiento de resultados (por nombre, precio o distancia) y la obtención
 * de la ubicación actual del usuario si se otorgan permisos.
 *
 * Este fragmento se comunica con {@link ProfesionalViewModel} y {@link CitasPendientesViewModel}
 * para obtener y mostrar los datos.
 */
public class BuscadorFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText searchInput, locationInput;
    private Spinner specialitySpinner;
    private ProfesionalViewModel profesionalViewModel;
    private CitasPendientesViewModel citasPendientesViewModel;
    private ProfesionalAdapter profesionalAdapter;
    private ChipGroup chipGroup;
    private ProgressBar progressBar;
    private MaterialButton searchButton;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;

    /**
     * Infla la vista del fragmento y configura los componentes de UI, ViewModels y eventos.
     *
     * @param inflater           el LayoutInflater
     * @param container          el ViewGroup padre
     * @param savedInstanceState el estado previamente guardado (si existe)
     * @return la vista raíz inflada
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buscador, container, false);

        initializeViews(rootView);
        setupViewModelsAndRecycler();
        setupSpinnerEspecialidad();
        setupListeners();
        loadInitialData();

        return rootView;
    }

    /**
     * Inicializa todos los componentes visuales del fragmento.
     *
     * @param rootView la vista raíz del fragmento
     */
    private void initializeViews(View rootView) {
        searchInput = rootView.findViewById(R.id.search_input_nombre);
        locationInput = rootView.findViewById(R.id.search_input_localidad);
        specialitySpinner = rootView.findViewById(R.id.spinner_especialidad);
        searchButton = rootView.findViewById(R.id.search_button);
        fabSort = rootView.findViewById(R.id.fab_ordenar);
        recyclerView = rootView.findViewById(R.id.recycler_view_buscador);
        progressBar = rootView.findViewById(R.id.progress_bar);
        chipGroup = rootView.findViewById(R.id.chip_group);
    }

    /**
     * Configura los ViewModels y el adaptador del RecyclerView, y enlaza los observables
     * de estado y datos profesionales.
     */
    private void setupViewModelsAndRecycler() {
        citasPendientesViewModel = new ViewModelProvider(requireActivity()).get(CitasPendientesViewModel.class);
        profesionalViewModel = new ViewModelProvider(this).get(ProfesionalViewModel.class);
        profesionalAdapter = new ProfesionalAdapter(profesionalViewModel, citasPendientesViewModel);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(profesionalAdapter);

        // Observador para la ubicación
        profesionalViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null && profesionalViewModel.getProfesionales().getValue() != null) {
                profesionalViewModel.ordenarPorDistancia(location);
            }
        });

        profesionalViewModel.getProfesionales().observe(getViewLifecycleOwner(), profesionalesList -> {
            profesionalAdapter.setProfesionales(profesionalesList);
        });

        profesionalViewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        profesionalViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case EMPTY:
                    Toast.makeText(getContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    String error = profesionalViewModel.getErrorMensaje().getValue();
                    if (error != null) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
    }

    /**
     * Configura el spinner de especialidades con una lista y escucha cambios de selección
     * para aplicar filtros automáticamente.
     */
    private void setupSpinnerEspecialidad() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                EspecialidadUtils.getEspecialidadesListWithDefault());

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialitySpinner.setAdapter(spinnerAdapter);

        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    combinarFiltros();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * Configura los listeners para acciones del usuario, como introducir texto,
     * pulsar botones o seleccionar un profesional.
     */
    private void setupListeners() {
        locationInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                combinarFiltros();
                ocultarTeclado(locationInput);
                return true;
            }
            return false;
        });

        searchButton.setOnClickListener(v -> {
            combinarFiltros();
            ocultarTeclado(searchInput);
        });

        profesionalAdapter.setOnItemClickListener(profesional -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombre", profesional.nombre);
            bundle.putString("direccion", profesional.direccion);
            bundle.putString("localidad", profesional.localidad);
            bundle.putString("imagen", profesional.getImagen());
            bundle.putString("telefono", profesional.telefono);
            bundle.putString("email", profesional.email);
            bundle.putString("especialidad", profesional.especialidad);
            bundle.putDouble("precio", profesional.precio);
            bundle.putString("horaInicio", profesional.horaInicio);
            bundle.putString("horaFin", profesional.horaFin);
            bundle.putDouble("latitud", profesional.latitud);
            bundle.putDouble("longitud", profesional.longitud);

            DetalleFragment detalleFragment = new DetalleFragment();
            detalleFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, detalleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        setupListenersOrdenamiento();
    }

    /**
     * Configura los listeners específicos para el ordenamiento de resultados mediante chips
     * de nombre, precio o distancia.
     */
    private void setupListenersOrdenamiento() {
        fabSort.setOnClickListener(v -> {
            int visibility = chipGroup.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            chipGroup.setVisibility(visibility);
        });

        Chip chipSortName = chipGroup.findViewById(R.id.chip_sort_nombre);
        Chip chipSortPrice = chipGroup.findViewById(R.id.chip_sort_precio);
        Chip chipSortDistance = chipGroup.findViewById(R.id.chip_sort_distancia);

        chipSortName.setOnClickListener(v -> {
            profesionalViewModel.ordenarPorNombre();
            chipGroup.setVisibility(View.GONE);
        });

        chipSortPrice.setOnClickListener(v -> {
            profesionalViewModel.ordenarPorPrecio();
            chipGroup.setVisibility(View.GONE);
        });

        chipSortDistance.setOnClickListener(v -> {
            Location location = profesionalViewModel.getCurrentLocation().getValue();
            if (location != null) {
                profesionalViewModel.ordenarPorDistancia(location);
                chipGroup.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });

        searchInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER && !event.isShiftPressed())) {
                combinarFiltros();
                ocultarTeclado(searchInput);
                return true;
            }
            return false;
        });
    }

    /**
     * Carga los datos iniciales de profesionales y solicita la ubicación del usuario.
     */
    private void loadInitialData() {
        profesionalViewModel.buscarProfesionales();
        requestLocationPermissionAndFetchLocation();
    }

    /**
     * Aplica los filtros de búsqueda combinando nombre, localidad y especialidad.
     */
    private void combinarFiltros() {
        String nombre = searchInput.getText().toString().trim();
        String localidad = locationInput.getText().toString().trim();
        String especialidadSeleccionada = specialitySpinner.getSelectedItem().toString();

        Long especialidadId = null;
        if (!especialidadSeleccionada.equals(EspecialidadUtils.getDefaultSpeciality())) {
            especialidadId = EspecialidadUtils.getEspecialidadesMap().get(especialidadSeleccionada);
        }

        profesionalViewModel.filtrarProfesionales(nombre, localidad, especialidadId);
    }

    /**
     * Oculta el teclado del sistema desde un determinado campo de entrada.
     *
     * @param view la vista que tiene el foco
     */
    private void ocultarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Solicita permisos de ubicación y, si se conceden, recupera la localización del usuario.
     */
    private void requestLocationPermissionAndFetchLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            profesionalViewModel.fetchLocation(requireContext());
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Manejador de la respuesta del usuario al diálogo de permisos.
     *
     * @param requestCode  el código de solicitud
     * @param permissions  los permisos solicitados
     * @param grantResults los resultados (permitido o denegado)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                profesionalViewModel.fetchLocation(requireContext());
            } else {
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}