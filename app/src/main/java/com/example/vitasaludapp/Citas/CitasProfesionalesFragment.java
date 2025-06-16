package com.example.vitasaludapp.Citas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vitasaludapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Fragmento que presenta las pestañas "Citas Pendientes" y "Citas Pasadas" dentro de un mismo contenedor.
 * Utiliza un {@link ViewPager2} para navegación lateral, y un {@link TabLayout} para mostrar las pestañas.
 */
public class CitasProfesionalesFragment extends Fragment {

    /**
     * Infla el layout del fragmento y configura el ViewPager y las pestañas.
     *
     * @return vista raíz del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_profesional, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        CitasPagerAdapter adapter = new CitasPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Citas Pendientes");
            } else {
                tab.setText("Citas Pasadas");
            }
        }).attach();

        return view;
    }
}