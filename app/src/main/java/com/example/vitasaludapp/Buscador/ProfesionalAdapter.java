package com.example.vitasaludapp.Buscador;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitasaludapp.Citas.CitasPendientesViewModel;
import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de profesionales sanitarios en un RecyclerView.
 * Utiliza {@link Profesional.Contenido} como modelo y permite mostrar nombre,
 * especialidad, precio, ubicación y foto.
 */
public class ProfesionalAdapter extends RecyclerView.Adapter<ProfesionalAdapter.ViewHolder> {

    private List<Profesional.Contenido> profesionales = new ArrayList<>();
    private ProfesionalViewModel profesionalViewModel;
    private CitasPendientesViewModel citasPendientesViewModel;
    private OnItemClickListener listener;

    /**
     * Interfaz para manejar clics en un elemento de la lista de profesionales.
     */
    public interface OnItemClickListener {
        void onItemClick(Profesional.Contenido profesional);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor del adaptador.
     *
     * @param profesionalViewModel       ViewModel que contiene la lógica de profesionales.
     * @param citasPendientesViewModel  ViewModel que contiene citas pendientes.
     */
    public ProfesionalAdapter(ProfesionalViewModel profesionalViewModel,
                              CitasPendientesViewModel citasPendientesViewModel) {
        this.profesionalViewModel = profesionalViewModel;
        this.citasPendientesViewModel = citasPendientesViewModel;
    }

    /**
     * Asigna la lista de profesionales a mostrar.
     *
     * @param profesionales lista de profesionales filtrados o completos.
     */
    public void setProfesionales(List<Profesional.Contenido> profesionales) {
        this.profesionales = profesionales;
        notifyDataSetChanged();
    }

    /**
     * Crea la vista del elemento y su correspondiente ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profesional, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Enlaza los datos del profesional a la vista del ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profesional.Contenido profesional = profesionales.get(position);
        holder.bind(profesional);

        Location currentLocation = profesionalViewModel.getCurrentLocation().getValue();
        if (currentLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    profesional.getLatitud(),
                    profesional.getLongitud(),
                    results
            );
            float distanceInKm = results[0] / 1000;
            holder.distanceTextView.setText(String.format("a %.1f km", distanceInKm));
        } else {
            holder.distanceTextView.setText("ubicación no disponible");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(profesional);
            }
        });
    }

    /**
     * Devuelve el número total de elementos en el adaptador.
     */
    @Override
    public int getItemCount() {
        return profesionales.size();
    }

    /**
     * ViewHolder que contiene las vistas individuales de cada profesional.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView localidad;
        private TextView precio;
        private TextView especialidad;
        private TextView distanceTextView;
        private ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_profesional);
            localidad = itemView.findViewById(R.id.localidad_profesional);
            precio = itemView.findViewById(R.id.precio_profesional);
            especialidad = itemView.findViewById(R.id.especialidad_profesional);
            imagen = itemView.findViewById(R.id.thumbnail);
            distanceTextView = itemView.findViewById(R.id.texto_distancia);
        }

        /**
         * Enlaza los datos del profesional a las vistas.
         *
         * @param profesional el profesional que se mostrará en este ViewHolder.
         */
        public void bind(Profesional.Contenido profesional) {
            nombre.setText(profesional.getNombre());
            localidad.setText(profesional.getLocalidad());
            especialidad.setText(profesional.getEspecialidad());
            precio.setText("Precio: " + profesional.getPrecio() + "€");

            Log.d("IMAGE_DEBUG", "Cargando imagen para: " + profesional.getNombre());
            Log.d("IMAGE_DEBUG", "URL de imagen: " + profesional.imagen);

            if (profesional.imagen != null && !profesional.imagen.isEmpty()) {
                Log.d("IMAGE_DEBUG", "Intentando cargar imagen desde URL");

                Glide.with(imagen.getContext())
                        .load(profesional.imagen)
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_star_favorite_full)
                        .into(imagen);
            } else {
                Log.d("IMAGE_DEBUG", "No hay URL de imagen, usando imagen por defecto");
                imagen.setImageResource(R.drawable.ic_star_favorite_empty);
            }
        }
    }
}