package com.example.vitasaludapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Clase utilitaria para trabajar con ubicación geográfica.
 * Maneja permisos, obtención de la última ubicación y cálculo de distancia entre coordenadas.
 */
public class LocationUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Verifica si el permiso de ubicación ha sido concedido.
     *
     * @param context contexto de la app.
     * @return true si el permiso de ubicación está otorgado.
     */
    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Obtiene de forma asíncrona la última ubicación conocida del dispositivo.
     *
     * @param context  contexto requerido para acceder al servicio de ubicación.
     * @param callback interface que recibe la respuesta o error.
     */
    @SuppressLint("MissingPermission")
    public static void getLastKnownLocation(Context context, LocationCallback callback) {
        if (hasLocationPermission(context)) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    callback.onLocationRetrieved(location);
                } else {
                    callback.onLocationError("No se pudo obtener la ubicación actual");
                }
            }).addOnFailureListener(e -> callback.onLocationError("Error al obtener la ubicación: " + e.getMessage()));
        } else {
            callback.onLocationError("Permiso de ubicación no concedido");
        }
    }

    /**
     * Calcula la distancia en kilómetros entre dos pares de coordenadas.
     *
     * @param lat1 latitud del punto A.
     * @param lon1 longitud del punto A.
     * @param lat2 latitud del punto B.
     * @param lon2 longitud del punto B.
     * @return distancia en kilómetros.
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Callback para operaciones asíncronas de localización.
     */
    public interface LocationCallback {
        void onLocationRetrieved(Location location);
        void onLocationError(String errorMessage);
    }
}