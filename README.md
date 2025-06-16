# VitaSalud

Aplicación Android para encontrar, agendar y gestionar citas médicas con profesionales sanitarios. Con una interfaz intuitiva, búsqueda avanzada, favoritos, geolocalización y notificaciones, VitaSalud facilita el acceso a la atención médica desde tu móvil.

## 📲 Funcionalidades

- 🔍 Búsqueda por nombre, especialidad y localidad
- 📅 Reservas de citas con selección de fecha y hora
- ⭐ Gestión de profesionales favoritos
- 📍 Ordenamiento por cercanía con ubicación en tiempo real
- 🗺️ Vista de detalles con mapa interactivo (OpenStreetMap)
- 🧾 Historial y cancelación de citas
- 🔔 Notificaciones locales tras cada reserva
- 🔐 Registro e inicio de sesión con Firebase
- 📧 Solicitud de alta como profesional sanitario

## 🧱 Estructura del Proyecto
```text
├── app/
│   ├── Buscador/              → Búsqueda y listado de profesionales
│   ├── Citas/                 → Citas pasadas, pendientes, adapters y ViewModels
│   ├── Detalles/              → Fragmento y lógica de detalle del profesional
│   ├── Favoritos/             → Visualización de favoritos
│   ├── Model/                 → Clases de datos: Profesional, Cita
│   ├── Sliders/               → Fragmentos de promociones del inicio
│   ├── Usuario/               → Login, registro, perfil y gestión de cuenta
│   └── utils/                 → Utilidades varias (ubicación, especialidades, notificaciones...)
```


## 🛠️ Tecnologías

- **Java 8** y **Android SDK 33+**
- **Firebase** (Auth, Realtime Database, Firestore)
- **Retrofit2** para conexión REST
- **OpenStreetMap (osmdroid)**
- **MVVM** con ViewModel + LiveData
- **Glide** para imágenes
- **Material Components** para UI moderna

## 🚀 Instalación local

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu_usuario/vitasalud.git
  ```
2. Abre con Android Studio.

3. Asegúrate de tener:
   - `google-services.json` en `app/`
   - Dispositivo/emulador con permisos de ubicación

4. Ejecuta y prueba.

> 💡 Requiere mínimo Android 8.0 (API 26)

## 📄 Licencia

Este proyecto está disponible bajo licencia MIT. Consulta el archivo `LICENSE` para más detalles.

## 📫 Contacto

Desarrollado por **Adrián**  
✉️ [adrdiaalf@alu.edu.gva.es](mailto:adrdiaalf@alu.edu.gva.es)




