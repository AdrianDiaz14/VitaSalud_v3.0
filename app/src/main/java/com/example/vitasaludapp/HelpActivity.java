package com.example.vitasaludapp;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que muestra una página de ayuda estática mediante un WebView.
 * Carga el archivo HTML localizado en la carpeta de assets del proyecto.
 */
public class HelpActivity extends AppCompatActivity {

    /**
     * Inicializa la actividad y carga el archivo help.html desde la carpeta assets.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/help.html");
    }
}