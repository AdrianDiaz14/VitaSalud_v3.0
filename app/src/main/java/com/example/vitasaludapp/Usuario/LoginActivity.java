package com.example.vitasaludapp.Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vitasaludapp.MainActivity;
import com.example.vitasaludapp.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad encargada de gestionar el inicio de sesión del usuario.
 * Permite iniciar sesión con correo y contraseña, redirigir al registro o recuperar contraseña.
 * Si el usuario ya ha iniciado sesión previamente, lo redirige directamente a la pantalla principal.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /**
     * Inicializa los componentes visuales y configura los listeners de los botones.
     * Verifica si existe una sesión activa.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegistrarse);
        TextView textForgotPassword = findViewById(R.id.textOlvideContraseña);

        // Ir a la pantalla de registro
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, NuevoUsuarioActivity.class);
            startActivity(intent);
        });

        // Recuperación de contraseña
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            iniciarSesion(email, password);
        });

        textForgotPassword.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            recuperarContrasena(email);
        });

    }

    /**
     * En el ciclo de vida, redirige automáticamente si el usuario ya está autenticado.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si hay sesión activa
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Redirigir al usuario a la escena principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Finalizar LoginActivity para que no pueda regresar
        }
    }

    /**
     * Inicia sesión con los datos proporcionados si son válidos.
     *
     * @param email    correo electrónico del usuario.
     * @param password contraseña del usuario.
     */
    private void iniciarSesion(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mostrarToast("Inicio de sesión exitoso");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            mostrarToast("Error: " + task.getException().getMessage());
                        }
                    });
        } else {
            mostrarToast("Completa todos los campos");
        }
    }

    /**
     * Envía un correo de recuperación de contraseña al email indicado.
     *
     * @param email correo del usuario que olvidó su contraseña.
     */
    private void recuperarContrasena(String email) {
        if (email.isEmpty()) {
            mostrarToast("Ingresa tu correo para recuperar la contraseña");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mostrarToast("Correo de recuperación enviado a " + email);
                    } else {
                        mostrarToast("Error: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * Muestra un mensaje corto (Toast) al usuario.
     *
     * @param mensaje mensaje a mostrar.
     */
    private void mostrarToast(String mensaje) {
        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}