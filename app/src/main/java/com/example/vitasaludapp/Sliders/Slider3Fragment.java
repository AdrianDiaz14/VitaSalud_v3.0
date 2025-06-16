package com.example.vitasaludapp.Sliders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vitasaludapp.R;

/**
 * Tercer y último fragmento del slider de promociones u ofertas.
 * No contiene navegación adicional, sirve como cierre visual del carrusel.
 */
public class Slider3Fragment extends Fragment {

    /**
     * Infla la vista del tercer slider sin interacción asociada.
     *
     * @return la vista del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slider3, container, false);
    }
}
