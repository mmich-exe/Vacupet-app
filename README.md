# VacuPet – Evaluación N.º 2 (Programación de Aplicaciones Móviles)

Evolución de tu app de la Evaluación 1: registro de mascotas con foto, vacunas y recordatorios de citas,
ahora con **Firebase Authentication, Room, ViewModel/UiState, Repository, ViewModelFactory y Navigation Compose**.

- **Problema:** los tutores de mascotas olvidan fechas de vacunas y controles.
- **Usuario objetivo:** personas que cuidan una o varias mascotas y necesitan centralizar su información y citas.
- **Objetivo:** centralizar los datos de cada mascota y su seguimiento veterinario.

## Antes de ejecutar: conectar Firebase (obligatorio para login/registro)

El `app/google-services.json` incluido es **un marcador de posición**: el proyecto compila, pero el login no funcionará hasta reemplazarlo.

1. <https://console.firebase.google.com> → **Agregar proyecto**.
2. **Agregar app → Android**, nombre del paquete: `com.example.vacupet`.
3. Descarga `google-services.json` y reemplaza el de la carpeta `app/`.
4. **Build → Authentication → Comenzar → Sign-in method** → habilita **Correo electrónico/contraseña**.
5. En Android Studio: **File → Sync Project with Gradle Files** y ejecuta en un emulador **con internet**.

Firebase se usa solo para autenticación (no Firestore ni Realtime Database).

## Por qué tu proyecto anterior no compilaba

El `Evaluacion1` original usaba una configuración de build muy nueva y poco común: Android Gradle Plugin 9.4.0,
Gradle 9.6, `compileSdk 37`, JDK 25 descargado vía foojay y `org.gradle.configuration-cache=true`.
Si tu Android Studio no tiene ese SDK/JDK/Gradle, el sync falla aunque el código esté bien.
Este proyecto usa AGP 8.7.3, Gradle 8.9, compileSdk 35 y JDK 17 (el que trae Android Studio).
Si Android Studio ofrece actualizar el plugin, puedes ignorarlo.

## Qué conserva de tu app original
Splash con logo, estilo verde con degradado, fotos de mascota (galería), cumpleaños con selector de fecha,
vacunas, información adicional y recordatorios de citas (fecha, motivo y descripción obligatorios).

## Qué se agregó para cumplir la pauta
- Login, registro, sesión persistente y logout con Firebase; pantallas protegidas.
- Datos en **Room** (antes se perdían al cerrar la app); cada usuario ve solo sus mascotas.
- **CRUD completo** de mascotas; recordatorios con crear/consultar/eliminar.
- Validaciones (nombre, especie, datos de la cita, correo y contraseña).
- Funcionalidad propia: búsqueda por nombre, filtro por especie y **Agenda** con citas próximas/pasadas de todas las mascotas.
- La foto se copia al almacenamiento interno para que persista tras cerrar la app.
- Pantallas: Splash, Login, Registro, Listado, Detalle (recibe ID), Formulario (crear/editar), Recordatorios (recibe ID), Agenda.

## Arquitectura
```
Compose (screens) → ViewModel (UiState) → Repository → DAO → Room
Firebase Auth → AuthViewModel (solo identifica al usuario y controla el acceso)
```
| Archivo | Para qué sirve |
|---|---|
| `data/Entidades.kt` | **Entity** `Mascota` y `Recordatorio` (con clave foránea y borrado en cascada). |
| `data/Daos.kt` | **DAO**: `@Insert`, `@Query` (Flow), `@Update`, `@Delete`. |
| `data/AppDatabase.kt` | **Database** Room (singleton). |
| `repository/AppRepository.kt` | Capa intermedia: la UI nunca toca el DAO. |
| `viewmodel/AppViewModel.kt` | CRUD, validaciones, búsqueda y filtro; expone `StateFlow<AppUiState>`. |
| `viewmodel/AppViewModelFactory.kt` | Construye `AppViewModel` con Repository y uid. |
| `viewmodel/AuthViewModel.kt` | Registro, login, recuperación de sesión y logout. |
| `navigation/AppNavigation.kt` | `NavHost`; elige pantalla según sesión y protege rutas. |

## Respuestas rápidas para la defensa
- **¿Dónde se determina si el usuario está autenticado?** `AuthViewModel.haySesion()` y `auth.uid` en `AppNavigation.kt`; sin uid no se crea `AppViewModel`.
- **¿Dónde se hace el `@Update`?** `MascotaDao.actualizar()` ← `AppRepository.actualizarMascota()` ← `AppViewModel.guardarMascota()` (cuando `id != null`).
- **¿Qué se ejecuta al presionar Guardar?** `FormularioScreen` → `onGuardar` → `AppViewModel.guardarMascota()` valida y luego inserta o actualiza.
- **¿Por qué Repository?** Separa el acceso a datos de la UI.
- **¿Para qué ViewModelFactory?** `AppViewModel` recibe Repository y uid; Android no sabe construirlo solo.
- **¿Dónde cambio el texto de un botón?** En el archivo de la pantalla correspondiente (`screens/`).

## Demostración mínima sugerida
1. Registrarse/iniciar sesión → 2. Navegar Listado / Agenda → 3. Registrar mascota → 4. Consultarla (listado y detalle) →
5. Editarla → 6. Eliminarla → 7. Cerrar y reabrir la app (datos y sesión persisten) → 8. Cerrar sesión.
