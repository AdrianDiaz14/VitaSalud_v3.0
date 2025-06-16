package com.example.vitasaludapp;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.vitasaludapp.Buscador.ProfesionalViewModel;
import com.example.vitasaludapp.Model.Profesional;
import com.example.vitasaludapp.utils.EspecialidadUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class BuscadorIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Profesional.Api mockApi;

    @Mock
    private Call<List<Profesional.Contenido>> mockCall;

    private MockedStatic<EspecialidadUtils> mockedEspecialidadUtils;
    private MockedStatic<android.util.Log> mockedLog;
    private ProfesionalViewModel profesionalViewModel;

    @Before
    public void setUp() {
        // Mockear Log y EspecialidadUtils
        mockedLog = mockStatic(android.util.Log.class);
        mockedLog.when(() -> android.util.Log.d(anyString(), anyString())).thenReturn(0);

        mockedEspecialidadUtils = mockStatic(EspecialidadUtils.class);

        // Configurar el mock de la API
        Profesional.api = mockApi;
        when(mockApi.obtenerProfesionales()).thenReturn(mockCall);

        profesionalViewModel = new ProfesionalViewModel();
    }

    @Test
    public void buscarProfesionales_integration() throws Exception {
        // 1. Configurar datos de prueba
        List<Profesional.Contenido> mockProfesionales = new ArrayList<>();
        mockProfesionales.add(new Profesional.Contenido(
                "Dr. Smith", "Calle 123", "Valencia",
                "46001", "123456789", "dr@example.com",
                "09:00", "18:00", 50.0, "image.jpg",
                39.4699, -0.3763, "Cardiología"));

        // 2. Configurar comportamiento del mock Call
        doAnswer(invocation -> {
            Callback<List<Profesional.Contenido>> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.success(mockProfesionales));
            return null;
        }).when(mockCall).enqueue(any(Callback.class));

        // 3. Observer para capturar el valor del LiveData
        final List<Profesional.Contenido>[] result = new List[1];
        CountDownLatch latch = new CountDownLatch(1);

        profesionalViewModel.getProfesionales().observeForever(profesionales -> {
            result[0] = profesionales;
            latch.countDown();
        });

        // 4. Ejecutar el método a testear
        profesionalViewModel.buscarProfesionales();

        // 5. Esperar a que se complete la llamada asíncrona
        latch.await(2, TimeUnit.SECONDS);

        // 6. Verificar los resultados
        assertNotNull(result[0]);
        assertEquals(1, result[0].size());
        assertEquals("Dr. Smith", result[0].get(0).getNombre());
    }

    @After
    public void tearDown() {
        // Cerrar los mocks estáticos
        mockedLog.close();
        mockedEspecialidadUtils.close();
    }
}