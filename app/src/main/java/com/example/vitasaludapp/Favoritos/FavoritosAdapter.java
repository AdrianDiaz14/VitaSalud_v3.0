package com.example.vitasaludapp.Favoritos;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.R;
import com.example.vitasaludapp.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adaptador para mostrar una lista de profesionales marcados como favoritos por el usuario.
 * Muestra información como nombre, localidad, especialidad, precio, distancia y foto.
 * Soporta ordenamiento por distancia en función de la ubicación del usuario.
 */
public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private List<Profesional.Contenido> profesionales = new ArrayList<>();
    private OnItemClickListener listener;
    private Location currentLocation;

    /**
     * Interfaz para manejar clics en un profesional de la lista.
     */
    public interface OnItemClickListener {
        void onItemClick(Profesional.Contenido profesional);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Asigna una nueva lista de profesionales favoritos al adaptador.
     *
     * @param profesionales lista actualizada de profesionales.
     */
    public void setProfesionales(List<Profesional.Contenido> profesionales) {
        this.profesionales = profesionales;
        notifyDataSetChanged();
    }

    /**
     * Crea y devuelve una nueva instancia de ViewHolder con el layout correspondiente.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profesional, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Asocia un profesional a la vista del ViewHolder y calcula su distancia si aplica.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profesional.Contenido profesional = profesionales.get(position);
        holder.bind(profesional);

        // Calcular y mostrar la distancia si se tiene la ubicación actual
        if (currentLocation != null) {
            double distance = LocationUtils.calculateDistance(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    profesional.getLatitud(),
                    profesional.getLongitud()
            );
            holder.distanceTextView.setText(String.format("a %.1f km", distance));
        } else {
            holder.distanceTextView.setText("error");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(profesional);
            }
        });
    }

    /**
     * Asigna la ubicación actual del usuario al adaptador. Si se indica, ordena la lista por distancia.
     *
     * @param location la ubicación actual.
     * @param ordenarPorDistancia si se debe reordenar la lista basada en distancia.
     */
    public void setCurrentLocation(Location location, boolean ordenarPorDistancia) {
        this.currentLocation = location;
        if (ordenarPorDistancia && profesionales != null && !profesionales.isEmpty()) {
            ordenarPorDistancia();
            notifyDataSetChanged();
        }
    }

    /**
     * Devuelve el número total de profesionales favoritos a mostrar.
     */
    @Override
    public int getItemCount() {
        return profesionales.size();
    }

    /**
     * ViewHolder interno que representa la vista de un profesional favorito.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private TextView localidad;
        private TextView precio;
        private TextView especialidad;
        private ImageView thumbnail;
        private TextView distanceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_profesional);
            localidad = itemView.findViewById(R.id.localidad_profesional);
            precio = itemView.findViewById(R.id.precio_profesional);
            especialidad = itemView.findViewById(R.id.especialidad_profesional);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            distanceTextView = itemView.findViewById(R.id.texto_distancia);
        }

        /**
         * Asocia los datos del profesional con las vistas del ítem.
         *
         * @param profesional profesional favorito que se mostrará.
         */
        public void bind(Profesional.Contenido profesional) {
            // Asignar valores a las vistas
            nombre.setText(profesional.getNombre());
            localidad.setText(profesional.getLocalidad());
            especialidad.setText(profesional.getEspecialidad());
            precio.setText("Precio: " + profesional.getPrecio() + "€");
            // Para la imagen, usa una librería como Glide o Picasso
            Glide.with(thumbnail.getContext())
                    .load(profesional.getImagen()) // URL de la imagen
                    .error(R.drawable.ic_star_favorite_empty) // Imagen en caso de error
                    .into(thumbnail);
        }
    }

    /**
     * Ordena internamente la lista de profesionales por distancia desde la ubicación actual.
     */
    private void ordenarPorDistancia() {
        if (currentLocation != null && profesionales != null && !profesionales.isEmpty()) {
            Collections.sort(profesionales, new Comparator<Profesional.Contenido>() {
                @Override
                public int compare(Profesional.Contenido p1, Profesional.Contenido p2) {
                    float[] result1 = new float[1];
                    float[] result2 = new float[1];
                    Location.distanceBetween(currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            p1.getLatitud(), p1.getLongitud(), result1);
                    Location.distanceBetween(currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            p2.getLatitud(), p2.getLongitud(), result2);
                    return Float.compare(result1[0], result2[0]);
                }
            });
        }
    }
}