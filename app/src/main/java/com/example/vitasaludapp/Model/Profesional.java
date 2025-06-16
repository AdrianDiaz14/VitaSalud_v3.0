package com.example.vitasaludapp.Model;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Modelo que representa un profesional sanitario.
 * Incluye atributos descriptivos, geográficos, de contacto y de especialidad.
 * Contiene también una interfaz API interna para obtener datos desde el servidor.
 */
public class Profesional {

    /**
     * Clase anidada que representa la respuesta con una lista de profesionales.
     */
    public static class Respuesta {
        List<Contenido> results;
    }

    /**
     * Clase anidada que representa los datos detallados de un profesional sanitario.
     */
    public static class Contenido {
        public String nombre;
        public String direccion;
        public String localidad;
        public String cp;
        public String telefono;
        public String email;
        public String horaInicio;
        public String horaFin;
        public double precio;
        public String imagen;
        public double latitud;
        public double longitud;
        public String especialidad;

        /**
         * Constructor vacío para Firebase y deserialización.
         */
        public Contenido() { }

        /**
         * Constructor completo.
         */
        public Contenido(String nombre, String direccion, String localidad,
                         String cp, String telefono, String email,
                         String horaInicio, String horaFin, double precio,
                         String imagen, double latitud, double longitud,
                         String especialidad) {
            this.nombre = nombre;
            this.direccion = direccion;
            this.localidad = localidad;
            this.cp = cp;
            this.telefono = telefono;
            this.email = email;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.precio = precio;
            this.imagen = imagen;
            this.latitud = latitud;
            this.longitud = longitud;
            this.especialidad = especialidad;
        }

        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getLocalidad() { return localidad; }
        public void setLocalidad(String localidad) { this.localidad = localidad; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }

        public String getImagen() { return imagen; }

        public String getDireccion() { return direccion; }

        public String getTelefono() { return telefono; }

        public String getHoraInicio() { return horaInicio; }

        public String getHoraFin() { return horaFin; }


        public double getLatitud() { return latitud; }

        public double getLongitud() { return longitud; }

        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

        /**
         * Determina si dos profesionales son lógicamente iguales por nombre y localidad.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true; // Si son el mismo objeto
            if (o == null || getClass() != o.getClass()) return false; // Si no son de la misma clase
            Contenido contenido = (Contenido) o;
            return Objects.equals(nombre, contenido.nombre) &&
                    Objects.equals(localidad, contenido.localidad); // Compara por los campos relevantes
        }

        // hashCode para mantener consistencia con equals
        @Override
        public int hashCode() {
            return Objects.hash(nombre, localidad);
        }
    }

    // Llamada a la API
    public static Api api = new Retrofit.Builder()
            .baseUrl("http://vitasalud.us-east-1.elasticbeanstalk.com:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api.class);

    /**
     * API estática para realizar peticiones HTTP a endpoints relacionados con profesionales.
     */
    public interface Api {
        /**
         * Obtiene todos los profesionales disponibles.
         */
        @GET("api/profesionales")
        Call<List<Contenido>> obtenerProfesionales();

        /**
         * Busca profesionales por nombre parcial.
         */
        @GET("api/profesionales/nombre/{nombre}")
        Call<List<Contenido>> buscarProfesionalPorNombre(@Path("nombre") String nombre);

        /**
         * Busca profesionales por localidad.
         */
        @GET("api/profesionales/localidad/{localidad}")
        Call<List<Profesional.Contenido>> buscarProfesionalPorLocalidad(@Path("localidad") String localidad);

        /**
         * Busca profesionales por especialidad.
         */
        @GET("api/profesionales/especialidad/{especialidadId}")
        Call<List<Profesional.Contenido>> buscarProfesionalPorEspecialidad(@Path("especialidadId") Long especialidadId);
    }
}