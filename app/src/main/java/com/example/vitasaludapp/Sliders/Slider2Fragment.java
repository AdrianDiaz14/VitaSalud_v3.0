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
 * Segundo fragmento del slider de ofertas.
 * Al hacer clic, redirige al fragmento de la tercera oferta.
 * Esta navegación permite crear una secuencia fluida en el carrusel.
 */
public class Slider2Fragment extends Fragment {

    /**
     * Infla la vista del slider 2 y configura la navegación al siguiente slider al hacer clic.
     *
     * @return vista raíz del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider2, container, false);

        view.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_oferta2Fragment_to_oferta3Fragment));

        return view;
    }
}
