package com.example.vitasaludapp.Usuario;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vitasaludapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

/**
 * Actividad de registro de nuevos usuarios.
 * Permite crear una cuenta con email y contraseña, almacenar el número de teléfono y
 * aceptar términos y condiciones.
 * Al finalizar, guarda el perfil en Firestore y redirige al login.
 */
public class NuevoUsuarioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editTextEmail, editTextPassword, editTextTelefono;
    private CheckBox checkBoxPoliticas;

    /**
     * Inicializa Firebase, vistas del formulario y los listeners de botones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        checkBoxPoliticas = findViewById(R.id.checkBoxPoliticas);
    }

    private void setupListeners() {
        TextView textViewPoliticas = findViewById(R.id.textViewPoliticas);
        Button buttonCrearCuenta = findViewById(R.id.buttonCrearCuenta);

        textViewPoliticas.setOnClickListener(v -> showTermsDialog());

        buttonCrearCuenta.setOnClickListener(v -> crearCuenta());
    }

    /**
     * Verifica campos y crea la cuenta en Firebase Authentication.
     */
    private void crearCuenta() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();

        if (!validarCampos(email, password, telefono)) return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        guardarDatosFirestore(email, telefono);
                    } else {
                        mostrarToast("Error: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Valida que los campos estén completos y que se hayan aceptado las políticas.
     *
     * @return true si todos los campos son válidos.
     */
    private boolean validarCampos(String email, String password, String telefono) {
        if (email.isEmpty() || password.isEmpty() || telefono.isEmpty()) {
            mostrarToast("Completa todos los campos");
            return false;
        }

        if (!checkBoxPoliticas.isChecked()) {
            mostrarToast("Debes aceptar las políticas de privacidad");
            return false;
        }

        return true;
    }

    /**
     * Guarda los datos del nuevo usuario en Firestore.
     *
     * @param email    email del usuario.
     * @param telefono número de teléfono.
     */
    private void guardarDatosFirestore(String email, String telefono) {
        String uid = mAuth.getCurrentUser().getUid();

        HashMap<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("telefono", telefono);

        db.collection("usuarios").document(uid)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    mostrarToast("Cuenta creada correctamente");
                    finish();
                })
                .addOnFailureListener(e -> {
                    mostrarToast("Error al guardar datos: " + e.getMessage());
                });
    }

    /**
     * Muestra un diálogo con los términos y condiciones de uso.
     */
    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_terminos_condiciones, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonAceptar = dialogView.findViewById(R.id.buttonAceptarTerminos);
        buttonAceptar.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Muestra un mensaje emergente al usuario.
     */
    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}