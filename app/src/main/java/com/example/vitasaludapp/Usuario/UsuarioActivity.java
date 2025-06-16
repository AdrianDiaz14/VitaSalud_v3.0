package com.example.vitasaludapp.Usuario;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.vitasaludapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad que permite al usuario gestionar su perfil.
 * Muestra su email y teléfono, permite cerrar sesión, cambiar contraseña y
 * solicitar un registro como profesional sanitario.
 */
public class UsuarioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Configura toolbar, carga datos del usuario y establece los listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewTelefono = findViewById(R.id.textViewTelefono);
        MaterialButton buttonChangePassword = findViewById(R.id.buttonChangePassword);
        MaterialButton buttonLogout = findViewById(R.id.buttonLogout);

        // Obtener el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Mostrar el email
            textViewEmail.setText(currentUser.getEmail());

            // Cargar el teléfono desde Firestore
            db.collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String telefono = documentSnapshot.getString("telefono");
                            textViewTelefono.setText(telefono != null ? telefono : "Teléfono no disponible");
                        } else {
                            Toast.makeText(UsuarioActivity.this, "Datos del usuario no encontrados", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UsuarioActivity.this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // Listener para cambiar contraseña
        buttonChangePassword.setOnClickListener(v -> {
            if (currentUser != null) {
                String email = currentUser.getEmail();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UsuarioActivity.this, "Correo enviado para cambiar la contraseña", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UsuarioActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Listener para cerrar sesión
        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(UsuarioActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finalizar UsuarioActivity
        });

        MaterialButton btnRegisterProfessional = findViewById(R.id.buttonRegistroProfesional);
        btnRegisterProfessional.setOnClickListener(v -> showProfessionalFormDialog());
    }

    /**
     * Lanza un diálogo con un formulario para solicitar ser profesional sanitario.
     * La solicitud es enviada vía correo electrónico.
     */
    private void showProfessionalFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_professional_form, null);
        builder.setView(dialogView);
        builder.setTitle("Registro de Profesional Sanitario");

        // Obtener referencias a los campos del formulario
        EditText etName = dialogView.findViewById(R.id.inputNombre);
        EditText etAddress = dialogView.findViewById(R.id.inputDireccion);
        EditText etCity = dialogView.findViewById(R.id.inputCiudad);
        EditText etPhone = dialogView.findViewById(R.id.inputTelefono);
        EditText etEmail = dialogView.findViewById(R.id.inputEmail);
        EditText etSchedule = dialogView.findViewById(R.id.inputHorario);
        EditText etPrice = dialogView.findViewById(R.id.inputPrecio);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.buttonEnvioSolicitud);

        AlertDialog dialog = builder.create();

        // Rellenar automáticamente el email si el usuario está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            etEmail.setText(currentUser.getEmail());
        }

        btnSubmit.setOnClickListener(v -> {
            // Validar campos
            if (etName.getText().toString().isEmpty() ||
                    etAddress.getText().toString().isEmpty() ||
                    etCity.getText().toString().isEmpty() ||
                    etPhone.getText().toString().isEmpty() ||
                    etEmail.getText().toString().isEmpty()) {

                Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Construir el mensaje del email
            String subject = "Solicitud de registro como profesional sanitario";
            String message = "Nueva solicitud de profesional sanitario:\n\n" +
                    "Nombre: " + etName.getText().toString() + "\n" +
                    "Dirección: " + etAddress.getText().toString() + "\n" +
                    "Localidad: " + etCity.getText().toString() + "\n" +
                    "Teléfono: " + etPhone.getText().toString() + "\n" +
                    "Email: " + etEmail.getText().toString() + "\n" +
                    "Horario: " + etSchedule.getText().toString() + "\n" +
                    "Precio consulta: " + etPrice.getText().toString();

            // Enviar email
            sendEmail(subject, message);
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Envía por email los datos recogidos del formulario de registro profesional.
     *
     * @param subject asunto del correo.
     * @param message cuerpo del mensaje.
     */
    private void sendEmail(String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"adrdiaalf@alu.edu.gva.es"});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo usando..."));
            Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No hay clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }
}