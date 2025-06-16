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
 * Fragmento contenedor que aloja las pestañas de citas pendientes y pasadas.
 * Configura {@link ViewPager2} y {@link TabLayout} usando {@link CitasPendientesHistorialAdapter}.
 */
public class CitasPendientesHistorialFragment extends Fragment {

    /**
     * Infla la vista y configura tabs y viewpager para navegación entre secciones.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas_profesional, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        CitasPendientesHistorialAdapter adapter = new CitasPendientesHistorialAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Citas pendientes");
                    break;
                case 1:
                    tab.setText("Citas pasadas");
                    break;
            }
        }).attach();

        return view;
    }
}
