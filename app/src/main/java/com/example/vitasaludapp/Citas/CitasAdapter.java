package com.example.vitasaludapp.Citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitasaludapp.Model.Cita;
import com.example.vitasaludapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de citas médicas en un RecyclerView.
 * Soporta tanto citas pendientes como citas pasadas. Permite gestionar eventos de clic
 * y eliminación (en el caso de citas pendientes).
 */
public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    private boolean isPending;

    /**
     * Constructor del adaptador.
     *
     * @param citasList lista de citas a mostrar.
     * @param isPending indica si las citas son pendientes (y por tanto, se pueden eliminar).
     */
    public CitasAdapter(List<Cita> citasList, boolean isPending) {
        this.citasList = citasList != null ? citasList : new ArrayList<>();
        this.isPending = isPending;
    }

    /**
     * Establece o actualiza la lista de citas mostradas.
     *
     * @param citasList nueva lista de citas.
     */
    public void setCitasList(List<Cita> citasList) {
        this.citasList = citasList != null ? citasList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Interfaz para manejar clics en un elemento de cita.
     */
    public interface OnItemClickListener {
        void onItemClick(Cita cita);
    }

    /**
     * Interfaz para manejar la acción de eliminación de una cita.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Cita cita);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    /**
     * Crea y devuelve un nuevo ViewHolder para una cita.
     */
    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    /**
     * Asocia los datos de una cita al ViewHolder correspondiente.
     */
    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.bind(cita, isPending);
    }

    /**
     * Devuelve el número total de citas mostradas.
     */
    @Override
    public int getItemCount() {
        return citasList.size();
    }

    /**
     * ViewHolder interno para contener los datos visuales de una cita.
     */
    class CitaViewHolder extends RecyclerView.ViewHolder {
        private TextView textFecha;
        private TextView textHora;
        private TextView textProfesional;
        private TextView textEspecialidad;
        private ImageButton buttonDelete;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            textFecha = itemView.findViewById(R.id.text_fecha_cita_pendiente);
            textHora = itemView.findViewById(R.id.text_hora_cita_pendiente);
            textProfesional = itemView.findViewById(R.id.text_nombre_profesional);
            textEspecialidad = itemView.findViewById(R.id.text_especialidad_profesional);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }

        /**
         * Asocia una cita con las vistas. Muestra botón de eliminar si es pendiente.
         *
         * @param cita       la cita a visualizar.
         * @param isPending  true si se debe permitir eliminación.
         */
        public void bind(Cita cita, boolean isPending) {
            textFecha.setText(cita.getFecha());
            textHora.setText(cita.getHora());
            textProfesional.setText(cita.getProfesional());
            textEspecialidad.setText(cita.getEspecialidad());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(cita);
                }
            });

            buttonDelete.setVisibility(isPending ? View.VISIBLE : View.GONE);
            if (isPending) {
                buttonDelete.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(cita);
                    }
                });
            }
        }
    }
}