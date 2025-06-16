package com.example.vitasaludapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.vitasaludapp.Buscador.BuscadorFragment;
import com.example.vitasaludapp.Citas.CitasPendientesHistorialFragment;
import com.example.vitasaludapp.Detalles.DetalleFragment;
import com.example.vitasaludapp.Favoritos.FavoritosFragment;
import com.example.vitasaludapp.Usuario.LoginActivity;
import com.example.vitasaludapp.Usuario.UsuarioActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad principal de la aplicación que actúa como contenedor de fragmentos y gestiona
 * la navegación, el menú lateral (NavigationDrawer), la barra de herramientas (Toolbar)
 * y el ciclo de usuario autenticado.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView headerTextView;
    private Toolbar toolbar;
    private NavigationView navigationView;

    /**
     * Inicializa la actividad y configura la barra de navegación, los fragmentos y el usuario autenticado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar el manejador de botón atrás
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 1. Cerrar drawer si está abierto
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                // 2. Manejar fragmentos en la pila
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);

                if (currentFragment instanceof HomeFragment) {
                    // 3. Si estamos en Home, preguntar si queremos salir
                    showExitConfirmation();
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    // 4. Navegar al fragmento anterior
                    getSupportFragmentManager().popBackStack();
                } else {
                    // 5. Mostrar confirmación de salida
                    showExitConfirmation();
                }
            }
        });

        // Verificar si el usuario está logueado
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Inicializar vistas
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerTextView = navigationView.getHeaderView(0).findViewById(R.id.textViewEmailUsuarioRegistrado);

        setSupportActionBar(toolbar);

        // Configurar el ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Mostrar email del usuario
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String emailUsuario = mAuth.getCurrentUser().getEmail();
            headerTextView.setText(emailUsuario != null ? emailUsuario : "Usuario sin email");
        } else {
            headerTextView.setText("Nuevo usuario");
        }

        // Listener para cambios en la pila de fragmentos
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateUI);

        // Configurar menú de navegación
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_inicio) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_usuario) {
                startActivity(new Intent(this, UsuarioActivity.class));
            } else if (itemId == R.id.nav_buscar) {
                selectedFragment = new BuscadorFragment();
            } else if (itemId == R.id.nav_citas) {
                selectedFragment = new CitasPendientesHistorialFragment();
            } else if (itemId == R.id.nav_favoritos) {
                selectedFragment = new FavoritosFragment();
            }

            if (selectedFragment != null) {
                navigateToFragment(selectedFragment, getTitleForMenuItem(itemId));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            navigateToFragment(new HomeFragment(), "VitaSalud");
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    /**
     * Actualiza el título y la navegación cuando cambia el fragmento actual.
     */
    private void updateUI() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        if (currentFragment instanceof HomeFragment) {
            updateToolbarAndNavigation("VitaSalud", R.id.nav_inicio);
        } else if (currentFragment instanceof BuscadorFragment) {
            updateToolbarAndNavigation("Buscador", R.id.nav_buscar);
        } else if (currentFragment instanceof FavoritosFragment) {
            updateToolbarAndNavigation("Favoritos", R.id.nav_favoritos);
        } else if (currentFragment instanceof CitasPendientesHistorialFragment) {
            updateToolbarAndNavigation("Citas", R.id.nav_citas);
        } else if (currentFragment instanceof DetalleFragment) {
        updateToolbarAndNavigation("Detalles", -1); // -1 para no marcar ningún ítem del menú
        }
    }

    /**
     * Actualiza el título de la barra superior y resalta el elemento del menú correspondiente.
     *
     * @param title      nuevo título a mostrar.
     * @param navItemId  ID del elemento del menú a marcar, o -1 para ninguno.
     */
    public void updateToolbarAndNavigation(String title, int navItemId) {
        toolbar.setTitle(title);
        navigationView.setCheckedItem(navItemId);
    }

    /**
     * Muestra un diálogo de confirmación al intentar salir de la app desde el Home.
     */
    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Estás seguro que quieres salir de la aplicación?")
                .setPositiveButton("Sí", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Reemplaza el fragmento actual por otro y actualiza el título y navegación.
     *
     * @param fragment fragmento de destino.
     * @param title    título para la barra superior.
     */
    public void navigateToFragment(Fragment fragment, String title) {
        // Si es DetalleFragment, forzar el título "Detalles"
        if (fragment instanceof DetalleFragment) {
            title = "Detalles";
        }

        // Limpiar la pila si estamos volviendo al Home
        if (fragment instanceof HomeFragment && getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(fragment instanceof HomeFragment ? null : title)
                .commit();

        updateToolbarAndNavigation(title, getMenuItemId(title));
    }

    /**
     * Devuelve el título textual asociado a un ID de menú.
     */
    private String getTitleForMenuItem(int itemId) {
        if (itemId == R.id.nav_inicio) return "VitaSalud";
        if (itemId == R.id.nav_buscar) return "Buscador";
        if (itemId == R.id.nav_citas) return "Citas";
        if (itemId == R.id.nav_favoritos) return "Favoritos";
        return "";
    }

    /**
     * Devuelve el ID de menú asociado a un título textual.
     */
    private int getMenuItemId(String title) {
        switch (title) {
            case "Buscador": return R.id.nav_buscar;
            case "Favoritos": return R.id.nav_favoritos;
            case "Citas": return R.id.nav_citas;
            case "VitaSalud": return R.id.nav_inicio;
            case "Detalles": return -1; // No hay ítem de menú para Detalles
            default: return -1;
        }
    }

    /**
     * Infla el menú superior de opciones.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Maneja las opciones del menú superior (Ayuda, Salir).
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Actualiza la navegación al volver a la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}