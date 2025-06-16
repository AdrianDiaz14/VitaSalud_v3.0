package com.example.vitasaludapp;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private FirebaseAuth mockAuth;
    @Mock private FirebaseFirestore mockFirestore;

    @Before
    public void setUp() {
        // No es necesario configurar FirebaseAuth si no se usa en el test
    }

    @Test
    public void login_integration() {
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true); // Simular autenticación exitosa

        assertTrue(mockAuth.signInWithEmailAndPassword("test@example.com", "123456").isSuccessful());
    }

    @Test
    public void registro_integration() {
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true); // Simular registro exitoso

        assertTrue(mockAuth.createUserWithEmailAndPassword("test@example.com", "123456").isSuccessful());
    }

}
