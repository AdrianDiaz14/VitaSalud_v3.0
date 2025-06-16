package com.example.vitasaludapp.utils;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;

/**
 * Validador personalizado para el selector de fechas de MaterialDatePicker.
 * Solo permite seleccionar fechas a partir del día siguiente y que no sean domingos.
 * Se utiliza como restricción en la reserva de citas.
 */
@SuppressLint("ParcelCreator")
public class CustomDateValidator implements CalendarConstraints.DateValidator {

    /**
     * Determina si una fecha dada en milisegundos es válida.
     *
     * @param date la fecha a validar en formato timestamp.
     * @return true si la fecha es válida (no es domingo y es posterior al día actual).
     */
    @Override
    public boolean isValid(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Verificar si la fecha es válida (a partir del día siguiente y no domingos)
        Calendar today = Calendar.getInstance();
        // Restablecer a medianoche del día actual
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.add(Calendar.DAY_OF_MONTH, 1); // A partir del día siguiente

        return dayOfWeek != Calendar.SUNDAY && date >= today.getTimeInMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
    }
}