package com.example.vitasaludapp.Citas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adaptador para ViewPager2 que permite cambiar entre pestañas de citas
 * pendientes y pasadas dentro del módulo de citas.
 */
public class CitasPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor del adaptador.
     *
     * @param fragment el Fragment padre que alberga este ViewPager2.
     */
    public CitasPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Crea el fragmento para la posición especificada.
     *
     * @param position 0 = CitasPendientesFragment, 1 = CitasPasadasFragment.
     * @return el Fragment correspondiente.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CitasPendientesFragment();
        } else {
            return new CitasPasadasFragment();
        }
    }

    /**
     * Devuelve la cantidad total de pestañas (2).
     */
    @Override
    public int getItemCount() {
        return 2; // Número de pestañas
    }
}