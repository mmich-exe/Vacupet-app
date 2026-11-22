# Implementación de "Comunidad Vacupet" (Mascotas Perdidas con Firestore, Cámara y GPS)

Este plan detalla la implementación de la funcionalidad **"Comunidad Vacupet"** para cumplir con todos los requisitos obligatorios de la **Evaluación 3**:
1. **Cloud Firestore**: CRUD completo de avisos de mascotas perdidas, persistencia y autenticación con Firebase Auth.
2. **Hardware (Cámara y GPS)**:
   - **Cámara**: Captura de fotografía de la mascota perdida, guardado local y almacenamiento de la URI local en Firestore (sin base64 en la nube).
   - **GPS**: Obtención de coordenadas de latitud y longitud actuales mediante `FusedLocationProviderClient`.
3. **API Externa**: Relación de los avisos con razas obtenidas de la API (TheDogAPI/TheCatAPI).
4. **Arquitectura**: ViewModel, Repository y Jetpack Compose.

## User Review Required

> [!IMPORTANT]
> Se agregarán las siguientes dependencias en `app/build.gradle.kts`:
> - `com.google.firebase:firebase-firestore`
> - `com.google.android.gms:play-services-location`

> [!NOTE]
> Se solicitarán permisos en `AndroidManifest.xml` para Cámara (`CAMERA`), Ubicación precisa (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`) y almacenamiento de imágenes.

## Open Questions

- ¿Deseas que los usuarios puedan eliminar o editar únicamente sus propios avisos (según el UID de Firebase Authentication)? *(Planificado: Sí, validando `userId`)*

## Proposed Changes

### Dependencias y Permisos
#### [MODIFY] [build.gradle.kts](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/build.gradle.kts)
- Agregar dependencias de Firestore y Location Services.

#### [MODIFY] [AndroidManifest.xml](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/AndroidManifest.xml)
- Agregar permisos de `CAMERA`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`.
- Configurar `FileProvider` para compartir URIs de fotos de la cámara.

---

### Capa de Datos y Repositorio
#### [NEW] [LostPetPost.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/data/model/LostPetPost.kt)
- Modelo de datos para Cloud Firestore (id, userId, userEmail, title, description, breedName, localPhotoUri, latitude, longitude, timestamp).

#### [NEW] [CommunityRepository.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/repository/CommunityRepository.kt)
- Repositorio para gestionar operaciones CRUD en Firestore (`lost_pets` collection).

---

### ViewModel y Lógica de Negocio
#### [NEW] [CommunityViewModel.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/viewmodel/CommunityViewModel.kt)
- ViewModel para manejar la lista de avisos, creación de avisos, obtención de ubicación GPS y estado de la cámara.

---

### Interfaz de Usuario y Navegación
#### [NEW] [ComunidadScreen.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/screens/ComunidadScreen.kt)
- Pantalla principal de la comunidad que lista los avisos de mascotas perdidas (con foto, datos de la API/raza, ubicación GPS y botón para agregar nuevo aviso).

#### [NEW] [CrearAvisoPerdidoScreen.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/screens/CrearAvisoPerdidoScreen.kt)
- Pantalla de formulario para crear un aviso: título, descripción, selección de raza (desde la API), botón para tomar foto con la cámara, botón para obtener ubicación GPS y botón de guardar.

#### [MODIFY] [Rutas.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/navigation/Rutas.kt)
- Agregar nuevas rutas para Comunidad (`comunidad`) y Crear Aviso (`crear_aviso`).

#### [MODIFY] [AppNavigation.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/navigation/AppNavigation.kt)
- Registrar las nuevas pantallas en el grafo de navegación.

#### [MODIFY] [InicioScreen.kt](file:///home/mich/Downloads/VacuPet_EVALUACION2/VacuPet/app/src/main/java/com/example/vacupet/screens/InicioScreen.kt)
- Agregar acceso directo o botón destacado hacia la "Comunidad Vacupet".

## Verification Plan

### Automated Tests
- Compilación del proyecto (`./gradlew assembleDebug`).

### Manual Verification
- Ejecutar la aplicación en el emulador.
- Navegar a "Comunidad Vacupet".
- Probar la creación de un aviso de mascota perdida capturando una foto (cámara simulada), obteniendo ubicación GPS y seleccionando una raza de la API.
- Verificar que el aviso se guarde en Cloud Firestore y persista al reiniciar la aplicación.
