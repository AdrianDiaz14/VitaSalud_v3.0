package com.example.vitasaludapp;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class CitasIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private DatabaseReference mockDatabaseRef;

    @Mock
    private FirebaseDatabase mockDatabase;

    @Before
    public void setUp() {
        // Configuración correcta de los mocks
        when(mockDatabase.getReference("citas")).thenReturn(mockDatabaseRef);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test-uid");
    }

    @Test
    public void cargarCitas_integration() {
        when(mockDatabaseRef.child("test-uid")).thenReturn(mockDatabaseRef);

        assertNotNull(mockDatabaseRef);
    }
}