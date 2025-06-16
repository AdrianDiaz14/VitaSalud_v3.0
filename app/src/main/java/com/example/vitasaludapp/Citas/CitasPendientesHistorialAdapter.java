package com.example.vitasaludapp.Citas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adaptador para ViewPager2 que permite cambiar entre las pestañas
 * de "Citas Pendientes" y "Citas Pasadas" dentro de la sección profesional.
 */
public class CitasPendientesHistorialAdapter extends FragmentStateAdapter {

    /**
     * Constructor del adaptador.
     *
     * @param fragmentActivity actividad contenedora del ViewPager.
     */
    public CitasPendientesHistorialAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Devuelve el fragmento correspondiente a la pestaña seleccionada.
     *
     * @param position 0: CitasPendientesFragment, 1: CitasPasadasFragment.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CitasPendientesFragment();
            case 1:
                return new CitasPasadasFragment();
            default:
                return new CitasPendientesFragment();
        }
    }

    /**
     * Devuelve el número total de pestañas.
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
