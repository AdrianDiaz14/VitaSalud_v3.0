package com.example.vitasaludapp.Detalles;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.vitasaludapp.MainActivity;
import com.example.vitasaludapp.Model.Cita;
import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.R;
import com.example.vitasaludapp.utils.CustomDateValidator;
import com.example.vitasaludapp.utils.NotificationHelper;
import com.example.vitasaludapp.utils.Utils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragmento que muestra la información detallada de un profesional sanitario.
 * Permite visualizar datos como imagen, dirección, contacto, especialidad, precio
 * y ubicación en el mapa. También permite marcar/desmarcar como favorito y agendar citas.
 */
public class DetalleFragment extends Fragment {

    private TextView nombre, localidad, direccion, telefono, email, especialidad, precio, horario;
    private ImageView imagen;
    private ImageButton buttonFavorite;
    private ExtendedFloatingActionButton buttonSelectDate;
    private Button buttonComoLlegar;
    private MapView mapView;
    private double latitud, longitud;

    private DetalleViewModel viewModel;

    /**
     * Infla la vista del fragmento, inicializa los componentes visuales, el mapa y
     * los datos del profesional, y configura los listeners.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle, container, false);

        viewModel = new ViewModelProvider(this).get(DetalleViewModel.class);

        initViews(view);
        setupMap();
        loadProfessionalData();
        setupClickListeners();
        observeViewModel();

        return view;
    }

    /**
     * Inicializa todas las vistas del layout.
     */
    private void initViews(View view) {
        nombre = view.findViewById(R.id.nombre_detalle);
        imagen = view.findViewById(R.id.profesional_image);
        direccion = view.findViewById(R.id.direccion_detalle);
        localidad = view.findViewById(R.id.localidad_detalle);
        telefono = view.findViewById(R.id.telefono_detalle);
        email = view.findViewById(R.id.email_detalle);
        especialidad = view.findViewById(R.id.especialidad_detalle);
        precio = view.findViewById(R.id.precio_detalle);
        horario = view.findViewById(R.id.horario_detalle);
        buttonFavorite = view.findViewById(R.id.button_favorite);
        buttonSelectDate = view.findViewById(R.id.fab_reservar_cita);
        mapView = view.findViewById(R.id.mapView);
        buttonComoLlegar = view.findViewById(R.id.button_como_llegar);
    }

    /**
     * Configura el mapa de OpenStreetMap para mostrar la ubicación del profesional.
     */
    private void setupMap() {
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0));
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
    }

    /**
     * Carga los datos del profesional desde los argumentos del Bundle
     * y los muestra en la interfaz gráfica.
     */
    private void loadProfessionalData() {
        Bundle args = getArguments();
        if (args != null) {
            String profesionalNombre = args.getString("nombre");
            String profesionalUrlImagen = args.getString("imagen");
            String profesionalDireccion = args.getString("direccion");
            String profesionalLocalidad = args.getString("localidad");
            String profesionalTelefono = args.getString("telefono");
            String profesionalEmail = args.getString("email");
            String profesionalEspecialidad = args.getString("especialidad");
            double profesionalPrecio = args.getDouble("precio");
            String profesionalHoraInicio = args.getString("horaInicio");
            String profesionalHoraFin = args.getString("horaFin");
            latitud = args.getDouble("latitud", 0.0);
            longitud = args.getDouble("longitud", 0.0);

            // Mostrar datos en la UI
            nombre.setText(profesionalNombre);
            direccion.setText(profesionalDireccion);
            localidad.setText(profesionalLocalidad);
            telefono.setText(profesionalTelefono);
            email.setText(profesionalEmail);
            especialidad.setText(profesionalEspecialidad);
            precio.setText(String.format("%.2f €", profesionalPrecio));
            horario.setText(String.format("Horario: %s - %s", profesionalHoraInicio, profesionalHoraFin));

            // Cargar imagen
            if (profesionalUrlImagen != null && !profesionalUrlImagen.isEmpty()) {
                Glide.with(requireContext())
                        .load(profesionalUrlImagen)
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_star_favorite_empty)
                        .into(imagen);
            } else {
                imagen.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // Configurar mapa
            GeoPoint punto = new GeoPoint(latitud, longitud);
            mapView.getController().setCenter(punto);

            Marker marcador = new Marker(mapView);
            marcador.setPosition(punto);
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcador.setTitle("Ubicación del profesional");
            mapView.getOverlays().add(marcador);

            // Verificar si es favorito
            viewModel.checkFavoriteStatus(profesionalNombre);
        }
    }

    /**
     * Configura los eventos de los botones: contacto, favoritos, reserva, etc.
     */
    private void setupClickListeners() {
        email.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            if (!emailText.isEmpty()) {
                abrirAppDeCorreo(emailText);
            }
        });

        telefono.setOnClickListener(v -> {
            String telefonoText = telefono.getText().toString();
            if (!telefonoText.isEmpty()) {
                abrirMarcador(telefonoText);
            }
        });

        buttonFavorite.setOnClickListener(v -> {
            Bundle args = getArguments();
            if (args != null) {
                Profesional.Contenido profesional = new Profesional.Contenido(
                        args.getString("nombre"),
                        args.getString("direccion"),
                        args.getString("localidad"),
                        "", // cp
                        args.getString("telefono"),
                        args.getString("email"),
                        args.getString("horaInicio"),
                        args.getString("horaFin"),
                        args.getDouble("precio"),
                        args.getString("imagen"),
                        args.getDouble("latitud"),
                        args.getDouble("longitud"),
                        args.getString("especialidad")
                );
                viewModel.toggleFavorite(profesional);
            }
        });

        buttonSelectDate.setOnClickListener(v -> showDatePicker());

        buttonComoLlegar.setOnClickListener(v -> {
            String uri = "https://www.google.com/maps/dir/?api=1&destination=" + latitud + "," + longitud + "&travelmode=driving";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "No se pudo abrir la aplicación de mapas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Observa los cambios de estado del profesional (como favorito) desde el ViewModel.
     */
    private void observeViewModel() {
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            if (isFavorite != null) {
                buttonFavorite.setImageResource(isFavorite ?
                        R.drawable.ic_star_favorite_full : R.drawable.ic_star_favorite_empty);
            }
        });
    }

    /**
     * Abre una aplicación de correo electrónico con la dirección del profesional.
     */
    private void abrirAppDeCorreo(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);
    }

    /**
     * Lanza un intent para abrir el marcador telefónico con el número del profesional.
     */
    private void abrirMarcador(String numeroTelefono) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + numeroTelefono));
        startActivity(intent);
    }

    /**
     * Muestra un diálogo para seleccionar una fecha usando un DatePicker de Material Design.
     */
    private void showDatePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(new CustomDateValidator());

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar día para tu cita")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String fechaSeleccionada = String.format("%1$tY-%1$tm-%1$td", calendar);

            Bundle args = getArguments();
            if (args != null) {
                mostrarHorasDisponibles(
                        fechaSeleccionada,
                        args.getString("nombre"),
                        args.getString("horaInicio"),
                        args.getString("horaFin"),
                        args.getString("especialidad")
                );
            }
        });

        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    /**
     * Filtra las horas disponibles del profesional para una fecha específica.
     */
    private void mostrarHorasDisponibles(String fecha, String profesionalNombre, String horaInicio, String horaFin, String profesionalEspecialidad) {
        DatabaseReference citasRef = FirebaseDatabase.getInstance()
                .getReference("citas").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        citasRef.orderByChild("fecha").equalTo(fecha).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> horasDisponibles = generarHorasDisponibles(horaInicio, horaFin);
                List<String> horasOcupadas = new ArrayList<>();

                for (DataSnapshot citaSnapshot : snapshot.getChildren()) {
                    Cita cita = citaSnapshot.getValue(Cita.class);
                    if (cita != null && profesionalNombre.equals(cita.getProfesional())) {
                        horasOcupadas.add(cita.getHora());
                    }
                }

                horasDisponibles.removeAll(horasOcupadas);
                mostrarDialogoHoras(fecha, profesionalNombre, horasDisponibles, profesionalEspecialidad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar horarios: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Genera una lista de horas de atención disponibles dentro del horario especificado.
     */
    private List<String> generarHorasDisponibles(String horaInicio, String horaFin) {
        List<String> horasDisponibles = new ArrayList<>();
        int apertura = convertirHoraAEntero(horaInicio);
        int cierre = convertirHoraAEntero(horaFin);

        for (int hora = apertura; hora < cierre; hora++) {
            horasDisponibles.add(hora + ":00");
            horasDisponibles.add(hora + ":30");
        }
        return horasDisponibles;
    }

    /**
     * Convierte una hora con formato "HH:mm" a valor entero (ej. "08:00" -> 8).
     */
    private int convertirHoraAEntero(String hora) {
        try {
            String[] partes = hora.split(":");
            return Integer.parseInt(partes[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Muestra un diálogo con las horas libres para reservar una cita.
     */
    private void mostrarDialogoHoras(String fecha, String profesionalNombre, List<String> horasDisponibles, String profesionalEspecialidad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona un horario");

        String[] horasArray = horasDisponibles.toArray(new String[0]);
        builder.setItems(horasArray, (dialog, which) -> {
            String horaSeleccionada = horasArray[which];
            guardarCitaEnFirebase(fecha, horaSeleccionada, profesionalNombre, profesionalEspecialidad);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    /**
     * Guarda una cita seleccionada por el usuario en Firebase y actualiza la disponibilidad.
     */
    private void guardarCitaEnFirebase(String fecha, String hora, String profesionalNombre, String profesionalEspecialidad) {
        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas");
        DatabaseReference citasOcupadasRef = FirebaseDatabase.getInstance().getReference("citas_ocupadas").child(fecha);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String citaId = citasRef.child(uid).push().getKey();

        if (citaId != null) {
            long timestamp = Utils.convertirFechaHoraATimestamp(fecha, hora);

            Cita nuevaCita = new Cita(fecha, hora, profesionalNombre, profesionalEspecialidad, timestamp);
            citasRef.child(uid).child(citaId).setValue(nuevaCita)
                    .addOnSuccessListener(aVoid -> {
                        citasOcupadasRef.child(hora).setValue(profesionalNombre)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Cita guardada correctamente", Toast.LENGTH_SHORT).show();
                                    NotificationHelper.sendOrderNotification(getContext());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al guardar cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Actualiza la barra de título si este fragmento está visible en MainActivity.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateToolbarAndNavigation("Detalles", -1);
        }
    }
}