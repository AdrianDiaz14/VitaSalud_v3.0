package com.example.vitasaludapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vitasaludapp.Buscador.BuscadorFragment;
import com.example.vitasaludapp.Favoritos.FavoritosFragment;

/**
 * Fragmento principal (inicio) de la aplicación.
 * Muestra un carrusel de sliders de ofertas, así como accesos directos a las secciones de búsqueda y favoritos.
 */
public class HomeFragment extends Fragment {

    private Handler handler;
    private Runnable runnable;
    private NavController navController;
    private int[] fragmentIds = {R.id.oferta1Fragment, R.id.oferta2Fragment, R.id.oferta3Fragment};
    private int currentIndex = 0;

    /**
     * Infla la vista del fragmento e inicializa el slider y botones de navegación.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configurar navegación de slideshow
        setupSlideshow(view);

        // Configurar botones
        setupButtons(view);

        return view;
    }

    /**
     * Configura el slider automático de ofertas que navega cada 4 segundos.
     */
    private void setupSlideshow(View view) {
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        navController = navHostFragment.getNavController();

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex >= fragmentIds.length) currentIndex = 0;
                navController.navigate(fragmentIds[currentIndex]);
                currentIndex++;
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(runnable, 4000);
    }

    /**
     * Configura los botones "Buscar" y "Favoritos" del menú principal.
     */
    private void setupButtons(View view) {
        Button btnBuscar = view.findViewById(R.id.btn_buscar);
        Button btnFavoritos = view.findViewById(R.id.btn_favoritos);

        btnBuscar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToFragment(new BuscadorFragment(), "Buscador");
            }
        });

        btnFavoritos.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToFragment(new FavoritosFragment(), "Favoritos");
            }
        });
    }

    /**
     * Restablece la barra de navegación cuando se reanuda el fragmento.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateToolbarAndNavigation("VitaSalud", R.id.nav_inicio);
        }
    }

    /**
     * Detiene el slider cuando el fragmento no está visible.
     */
    @Override
    public void onPause() {
        super.onPause();
        // Detener el slideshow cuando el fragmento no está visible
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * Libera recursos del slider al destruirse la vista.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpiar recursos
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }
}