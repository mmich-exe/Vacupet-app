# 🐾 VacuPet

**VacuPet** es una aplicación Android para que los tutores de mascotas centralicen la información de sus animales —datos, foto, vacunas y citas veterinarias— y no olviden fechas importantes como vacunas y controles. Además, incluye la sección **Comunidad Vacupet** para reportar mascotas perdidas integrando Cloud Firestore, Cámara y GPS.

Desarrollada con **Kotlin** y **Jetpack Compose**, con autenticación mediante **Firebase Authentication**, base de datos local con **Room** y **Cloud Firestore**.

> Proyecto académico de la asignatura *Programación de Aplicaciones Móviles* (Evaluación 3) — INACAP.

---

## 📋 Contenido

- [Problema que resuelve](#-problema-que-resuelve)
- [Funcionalidades](#-funcionalidades)
- [Comunidad Vacupet (Evaluación 3)](#-comunidad-vacupet-evaluación-3)
- [Pantallas](#-pantallas)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Requisitos](#-requisitos)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Autoría](#-autoría)

---

## 🎯 Problema que resuelve

Quienes cuidan una o varias mascotas suelen olvidar las fechas de vacunas y controles, y tienen su información repartida en papeles, chats o recuerdos. VacuPet reúne todo en un solo lugar y, a través de su comunidad, permite reportar y buscar mascotas perdidas geolocalizadas.

**Usuario objetivo:** personas que cuidan una o más mascotas y necesitan organizar su información, seguimiento veterinario y apoyo comunitario.

---

## ✨ Funcionalidades

**Cuenta y acceso**
- Registro e inicio de sesión con correo y contraseña.
- Autenticación persistente con Firebase Authentication.

**Perfil del usuario**
- Tarjeta de perfil con foto, nombre, correo, teléfono, fecha de nacimiento y ciudad.
- Resumen de actividad (mascotas, favoritas, citas próximas).

**Mascotas (Room)**
- CRUD completo de mascotas con foto, especie, cumpleaños y vacunas.
- Búsqueda, filtros y favoritos.

**Recordatorios y Agenda**
- Citas por mascota y agenda general de próximas visitas veterinarias.

---

## 🌐 Comunidad Vacupet (Evaluación 3)

Sección colaborativa para reportar **mascotas perdidas**, integrando tecnologías avanzadas:
- **Cloud Firestore:** Sincronización en tiempo real (*Snapshot Listeners*) de avisos publicados por diferentes usuarios y cuentas.
- **Hardware - Cámara:** Captura de fotografías de la mascota perdida almacenadas de forma local y enlazadas al aviso.
- **Hardware - GPS:** Obtención automática de las coordenadas de latitud y longitud actuales mediante `FusedLocationProviderClient`.
- **API Externa:** Selección de razas de perros y gatos obtenidas directamente desde **TheDogAPI** y **TheCatAPI**, además de la opción de registrar otras especies.

---

## 📱 Pantallas

| Pantalla | Descripción |
|---|---|
| **Splash** | Presentación con logo y créditos de autoría. |
| **Login / Registro** | Autenticación de usuarios. |
| **Menú principal** | Accesos a mascotas, recordatorios, favoritos y Comunidad Vacupet. |
| **Listado y Detalle** | Gestión y visualización de las mascotas personales. |
| **Comunidad** | Listado en tiempo real de avisos de mascotas perdidas con fotos, ubicación GPS y botón de actualizar. |
| **Crear Aviso** | Formulario con selector de categoría (Perro/Gato/Otro), razas de la API, captura con cámara y geolocalización GPS. |
| **Perfil** | Gestión de datos personales del usuario. |

---

## 🛠 Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.0 |
| Interfaz | Jetpack Compose + Material Design 3 |
| Navegación | Navigation Compose |
| Estado | ViewModel + StateFlow + UiState |
| Base de datos local | Room 2.6 (con KSP) |
| Base de datos nube | Cloud Firestore (Tiempo real) |
| Autenticación | Firebase Authentication |
| Hardware / Sensores | Cámara (`FileProvider`) + GPS (`Play Services Location`) |
| API Externa | Retrofit + TheDogAPI & TheCatAPI |
| SDK | minSdk 26 · targetSdk / compileSdk 35 |

---

## 📁 Estructura del proyecto

```
app/src/main/java/com/example/vacupet/
├── MainActivity.kt
├── data/
│   ├── Entidades.kt
│   ├── Daos.kt
│   ├── AppDatabase.kt
│   └── model/              # Modelos de Firestore y APIs (Breed, CatBreed, LostPetPost)
├── repository/             # AppRepository, DogRepository, CatRepository, CommunityRepository
├── viewmodel/              # ViewModels con manejo de estados y corrutinas
├── navigation/             # Rutas y grafo de navegación
├── components/             # Componentes reutilizables de UI
└── screens/                # Vistas de la aplicación (incluyendo Comunidad y CrearAviso)
```

---

## 🚀 Instalación y ejecución

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/mmich-exe/Vacupet-app.git
   ```
2. **Abre el proyecto** en Android Studio.
3. **Configura Firebase**
   - Agrega tu archivo `google-services.json` en la carpeta `app/`.
   - Habilita **Authentication** (Correo/Contraseña) y crea una base de datos en **Cloud Firestore** en modo de prueba.
4. **Ejecuta** la aplicación en un emulador o dispositivo.

---

## 👩‍💻 Autoría

Desarrollado por:
- **Michele** ([@mmich-exe](https://github.com/mmich-exe))
- **Christopher** ([@Multi-Fruit](https://github.com/Multi-Fruit))

*Asignatura: Programación de Aplicaciones Móviles — INACAP.*
