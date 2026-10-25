# 🐾 VacuPet

**VacuPet** es una aplicación Android para que los tutores de mascotas centralicen la información de sus animales —datos, foto, vacunas y citas veterinarias— y no olviden fechas importantes como vacunas y controles.

Desarrollada con **Kotlin** y **Jetpack Compose**, con autenticación mediante **Firebase Authentication** y almacenamiento local con **Room**.

> Proyecto académico de la asignatura *Programación de Aplicaciones Móviles* — INACAP.

---

## 📋 Contenido

- [Problema que resuelve](#-problema-que-resuelve)
- [Funcionalidades](#-funcionalidades)
- [Pantallas](#-pantallas)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Modelo de datos](#-modelo-de-datos)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Requisitos](#-requisitos)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Autoría](#-autoría)

---

## 🎯 Problema que resuelve

Quienes cuidan una o varias mascotas suelen olvidar las fechas de vacunas y controles, y tienen su información repartida en papeles, chats o recuerdos. VacuPet reúne todo en un solo lugar, asociado a la cuenta de cada usuario, y permite consultar de un vistazo qué citas vienen próximamente.

**Usuario objetivo:** personas que cuidan una o más mascotas y necesitan organizar su información y su seguimiento veterinario.

---

## ✨ Funcionalidades

**Cuenta y acceso**
- Registro e inicio de sesión con correo y contraseña.
- Recuperación de sesión: al reabrir la app se entra directamente, sin volver a iniciar sesión.
- Recuperación de contraseña por correo electrónico.
- Cierre de sesión con confirmación.

**Perfil del usuario**
- Tarjeta de perfil con foto, nombre, correo, teléfono, fecha de nacimiento y ciudad.
- Edición de los datos del perfil desde un formulario independiente.
- Resumen de actividad (mascotas, favoritas y citas próximas).

**Mascotas**
- Registro, consulta, edición y eliminación (CRUD completo).
- Foto desde la galería, especie, cumpleaños, vacunas e información adicional.
- Búsqueda por nombre y filtro por especie.
- Mascotas favoritas, marcadas con una estrella, con su propia sección.

**Recordatorios**
- Citas por mascota con fecha, motivo y descripción.
- Agenda general con las citas próximas y pasadas de todas las mascotas.
- La próxima cita de cada mascota se muestra directamente en su tarjeta.

**Calidad**
- Validación de formularios con mensajes de error en pantalla.
- Datos guardados en el dispositivo, separados por usuario.
- Interfaz 100 % en Jetpack Compose con Material Design 3.

---

## 📱 Pantallas

| Pantalla | Descripción |
|---|---|
| **Splash** | Presentación con el logo; decide si ir al login o al menú según la sesión. |
| **Login** | Inicio de sesión y acceso a la recuperación de contraseña. |
| **Registro** | Creación de cuenta con correo y contraseña. |
| **Menú principal** | Tarjeta de perfil y accesos a mascotas, recordatorios y favoritos. |
| **Mis hijos animales** | Listado con búsqueda, filtro por especie, favoritos y botón para registrar. |
| **Detalle de mascota** | Información completa, citas, favorito, edición y eliminación. |
| **Formulario de mascota** | Registro y edición de una mascota. |
| **Recordatorios** | Alta y baja de citas de una mascota. |
| **Agenda** | Citas próximas y pasadas de todas las mascotas. |
| **Favoritos** | Mascotas marcadas con estrella. |
| **Perfil** | Vista de los datos del usuario y cierre de sesión. |
| **Editar perfil** | Formulario con los datos personales y la foto. |

---

## 🛠 Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.0 |
| Interfaz | Jetpack Compose + Material Design 3 |
| Navegación | Navigation Compose (con paso de parámetros) |
| Estado | ViewModel + StateFlow + UiState |
| Base de datos local | Room 2.6 (con KSP) |
| Autenticación | Firebase Authentication (correo y contraseña) |
| Asincronía | Coroutines y Flow |
| Build | Gradle (Kotlin DSL), Android Gradle Plugin 8.7 |
| SDK | minSdk 26 · targetSdk / compileSdk 35 |

---

## 🏗 Arquitectura

La app separa responsabilidades en capas, con un flujo de datos en un solo sentido:

```
Compose (screens)  →  ViewModel (UiState)  →  Repository  →  DAO  →  Room
        ▲                                                              │
        └────────────────── Flow (la UI se actualiza sola) ◄──────────┘

Firebase Authentication  →  AuthViewModel  (identifica al usuario y controla el acceso)
```

- **UI (Compose):** pantallas sin lógica de negocio; reciben estado y emiten eventos.
- **ViewModel:** contiene la lógica, las validaciones y expone un `StateFlow` con el estado de la pantalla.
- **ViewModelFactory:** construye el `AppViewModel` entregándole el repositorio y el identificador del usuario.
- **Repository:** única puerta de acceso a los datos; la UI nunca usa los DAO directamente.
- **Room:** persistencia local; cada registro pertenece al `uid` del usuario autenticado.
- **Firebase Authentication:** se usa exclusivamente para autenticar; los datos no se almacenan en la nube.

---

## 🗄 Modelo de datos

| Tabla | Descripción | Campos principales |
|---|---|---|
| `mascotas` | Mascotas de cada usuario | id, userId, nombre, especie, fotoPath, cumpleanos, vacunas, infoExtra, favorito |
| `recordatorios` | Citas de una mascota (se eliminan en cascada con ella) | id, userId, mascotaId, fecha, motivo, descripcion |
| `perfiles` | Datos personales del usuario | userId (uid de Firebase), nombre, telefono, cumpleanos, ciudad, fotoPath |

La base de datos usa **migraciones** de Room para conservar los datos al actualizar su estructura.

---

## 📁 Estructura del proyecto

```
app/src/main/java/com/example/vacupet/
├── MainActivity.kt
├── data/
│   ├── Entidades.kt          # Mascota, Recordatorio, Perfil
│   ├── Daos.kt               # MascotaDao, RecordatorioDao, PerfilDao
│   └── AppDatabase.kt        # Base de datos Room y migraciones
├── repository/
│   └── AppRepository.kt
├── viewmodel/
│   ├── AppUiState.kt         # Estado de la UI y errores de validación
│   ├── AppViewModel.kt       # CRUD, búsqueda, filtros, favoritos, perfil
│   ├── AppViewModelFactory.kt
│   ├── AuthUiState.kt
│   └── AuthViewModel.kt      # Registro, login, sesión y recuperación
├── navigation/
│   ├── Rutas.kt
│   └── AppNavigation.kt      # NavHost y protección de rutas
├── components/
│   ├── Comunes.kt            # Foto, barra superior, fondo y selector de fecha
│   └── MascotaCard.kt
├── screens/                  # Una pantalla por archivo
└── ui/theme/
    └── Theme.kt
```

---

## ✅ Requisitos

- **Android Studio** reciente (con JDK 17 o superior, incluido en el IDE).
- Un **emulador o dispositivo** con Android 8.0 (API 26) o superior.
- Una cuenta de **Firebase** (gratuita) con conexión a internet para el login.

---

## 🚀 Instalación y ejecución

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/mmich-exe/Vacupet-app.git
   ```
2. **Abre el proyecto** en Android Studio (`File → Open`) y espera a que Gradle sincronice.
3. **Configura Firebase**
   1. En la [consola de Firebase](https://console.firebase.google.com) crea un proyecto y agrega una app Android con el nombre de paquete `com.example.vacupet`.
   2. Descarga el archivo `google-services.json` y colócalo en la carpeta `app/`.
   3. En **Authentication → Sign-in method**, habilita **Correo electrónico/contraseña**.
4. **Ejecuta** la aplicación (▶) en un emulador o dispositivo con internet.

> El archivo `google-services.json` identifica tu proyecto de Firebase. Si el repositorio es público, considera mantenerlo fuera del control de versiones.

---

## 👩‍💻 Autoría

Desarrollado por **Michele** ([@mmich-exe](https://github.com/mmich-exe)) como proyecto de la asignatura Programación de Aplicaciones Móviles, INACAP.
Desarrollado por **Christopher** ([@Multi-Fruit](https://github.com/Multi-Fruit)) como proyecto de la asignatura Programación de Aplicaciones Móviles, INACAP.