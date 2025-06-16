package com.example.vitasaludapp.Citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.R;

import java.util.List;

/**
 * Adaptador para mostrar profesionales con citas pendientes en un RecyclerView.
 * Se utiliza en la vista de favoritos o historial del usuario, permitiendo
 * eliminar una cita con un botón visual.
 */
public class CitasPendientesAdapter extends RecyclerView.Adapter<CitasPendientesAdapter.ViewHolder> {

    private List<Profesional.Contenido> items;
    private OnItemDeleteClickListener onItemDeleteClickListener;

    /**
     * Interfaz para manejar eventos de eliminación de un profesional con cita pendiente.
     */
    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(Profesional.Contenido item);
    }

    /**
     * Constructor del adaptador.
     *
     * @param items                    lista de profesionales con cita pendiente.
     * @param onItemDeleteClickListener callback para manejo de eliminación.
     */
    public CitasPendientesAdapter(List<Profesional.Contenido> items, OnItemDeleteClickListener onItemDeleteClickListener) {
        this.items = items;
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout correspondiente.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_citas_pendientes, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Asocia los datos del profesional al ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profesional.Contenido item = items.get(position);
        holder.bind(item);
    }

    /**
     * Devuelve el número de elementos a mostrar.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Actualiza la lista de profesionales mostrada.
     */
    public void setItems(List<Profesional.Contenido> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder que contiene las vistas y lógica de interacción para cada profesional.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView title;
        private TextView price;
        private ImageView imageDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.nombre_profesional);
            price = itemView.findViewById(R.id.price);
            imageDelete = itemView.findViewById(R.id.image_delete);

            imageDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemDeleteClickListener != null) {
                    onItemDeleteClickListener.onItemDeleteClick(items.get(position));
                }
            });
        }

        /**
         * Asocia los datos del profesional con las vistas del ítem.
         *
         * @param item profesional con cita pendiente.
         */
        public void bind(Profesional.Contenido item) {
            title.setText(item.nombre);
            price.setText(String.format("%s€", item.precio));
            //Glide.with(thumbnail.getContext()).load(item.thumb).into(thumbnail);
        }
    }
}
