package com.example.vitasaludapp.Sliders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.vitasaludapp.R;

/**
 * Primer fragmento del slider de ofertas mostrado en el Home.
 * Al hacer clic en cualquier parte del fragmento, navega automáticamente al segundo slider.
 * Utiliza Navigation Component para realizar la transición.
 */
public class Slider1Fragment extends Fragment {

    /**
     * Infla el layout del slider 1 y configura el clic para navegar al slider 2.
     *
     * @return vista raíz inflada del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider1, container, false);

        view.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_oferta1Fragment_to_oferta2Fragment));

        return view;
    }
}
