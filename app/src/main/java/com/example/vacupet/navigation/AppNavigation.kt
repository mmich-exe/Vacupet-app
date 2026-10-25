package com.example.vacupet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vacupet.repository.AppRepository
import com.example.vacupet.screens.AgendaScreen
import com.example.vacupet.screens.DetalleScreen
import com.example.vacupet.screens.FavoritosScreen
import com.example.vacupet.screens.FormularioScreen
import com.example.vacupet.screens.InicioScreen
import com.example.vacupet.screens.ListadoScreen
import com.example.vacupet.screens.LoginScreen
import com.example.vacupet.screens.PerfilEditarScreen
import com.example.vacupet.screens.PerfilScreen
import com.example.vacupet.screens.RecordatoriosScreen
import com.example.vacupet.screens.RegistroScreen
import com.example.vacupet.screens.SplashScreen
import com.example.vacupet.viewmodel.AppViewModel
import com.example.vacupet.viewmodel.AppViewModelFactory
import com.example.vacupet.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    repository: AppRepository,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val auth by authViewModel.uiState.collectAsStateWithLifecycle()

    // Un AppViewModel por usuario (key = uid), compartido por todas las pantallas
    // autenticadas. Si no hay sesión es null y las pantallas protegidas no se dibujan.
    val appViewModel: AppViewModel? = auth.uid?.let { uid ->
        viewModel<AppViewModel>(key = uid, factory = AppViewModelFactory(repository, uid))
    }

    // Reacciona a cambios de sesión: login -> Menú principal, logout -> Login.
    LaunchedEffect(auth.uid) {
        val rutaActual = navController.currentBackStackEntry?.destination?.route ?: return@LaunchedEffect
        val enPantallaDeAcceso = rutaActual == Rutas.LOGIN || rutaActual == Rutas.REGISTRO
        if (auth.uid == null && !enPantallaDeAcceso && rutaActual != Rutas.SPLASH) {
            navController.navigate(Rutas.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else if (auth.uid != null && enPantallaDeAcceso) {
            navController.navigate(Rutas.INICIO) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        // ---------- Splash y acceso ----------
        composable(Rutas.SPLASH) {
            SplashScreen(
                onTerminar = {
                    // Si Firebase tiene una sesión guardada, se entra directo al menú.
                    val destino = if (authViewModel.haySesion()) Rutas.INICIO else Rutas.LOGIN
                    navController.navigate(destino) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                state = auth,
                onLogin = authViewModel::login,
                onIrARegistro = {
                    authViewModel.limpiarError()
                    navController.navigate(Rutas.REGISTRO)
                },
                onRecuperarPassword = authViewModel::recuperarPassword
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                state = auth,
                onRegistrar = authViewModel::registrar,
                onVolver = {
                    authViewModel.limpiarError()
                    navController.popBackStack()
                }
            )
        }

        // ---------- Pantallas protegidas (requieren sesión) ----------

        // Menú principal: perfil + accesos a mascotas, recordatorios y favoritos
        composable(Rutas.INICIO) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            InicioScreen(
                email = auth.email.orEmpty(),
                state = state,
                onMascotas = { navController.navigate(Rutas.LISTADO) },
                onRecordatorios = { navController.navigate(Rutas.AGENDA) },
                onFavoritos = { navController.navigate(Rutas.FAVORITOS) },
                onPerfil = { navController.navigate(Rutas.PERFIL) }
            )
        }

        composable(Rutas.LISTADO) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            ListadoScreen(
                state = state,
                onBusquedaChange = vm::cambiarBusqueda,
                onFiltroEspecie = vm::cambiarFiltroEspecie,
                onAbrirDetalle = { id -> navController.navigate(Rutas.detalle(id)) },
                onEditar = { id -> navController.navigate(Rutas.formulario(id)) },
                onFavorito = vm::alternarFavorito,
                onNueva = { navController.navigate(Rutas.formulario()) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.FAVORITOS) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            FavoritosScreen(
                state = state,
                onAbrirDetalle = { id -> navController.navigate(Rutas.detalle(id)) },
                onEditar = { id -> navController.navigate(Rutas.formulario(id)) },
                onFavorito = vm::alternarFavorito,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.IntType })
        ) { entry ->
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            val id = entry.arguments?.getInt(Rutas.ARG_ID) ?: -1
            val mascota = state.mascotaPorId(id)
            DetalleScreen(
                mascota = mascota,
                citas = state.recordatoriosDe(id),
                onVolver = { navController.popBackStack() },
                onEditar = { navController.navigate(Rutas.formulario(id)) },
                onRecordatorios = { navController.navigate(Rutas.recordatorios(id)) },
                onFavorito = { mascota?.let(vm::alternarFavorito) },
                onEliminar = {
                    mascota?.let(vm::eliminarMascota)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Rutas.FORMULARIO,
            arguments = listOf(navArgument(Rutas.ARG_ID) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { entry ->
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            val id = entry.arguments?.getInt(Rutas.ARG_ID) ?: -1
            val mascota = state.mascotaPorId(id)

            if (id != -1 && mascota == null && state.cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                FormularioScreen(
                    mascota = mascota,
                    errores = state.erroresMascota,
                    onGuardar = { nombre, especie, fotoPath, cumpleanos, vacunas, infoExtra ->
                        vm.guardarMascota(
                            id = id.takeIf { it != -1 },
                            nombre = nombre,
                            especie = especie,
                            fotoPath = fotoPath,
                            cumpleanos = cumpleanos,
                            vacunas = vacunas,
                            infoExtra = infoExtra,
                            onGuardado = { navController.popBackStack() }
                        )
                    },
                    onLimpiarErrores = vm::limpiarErroresMascota,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Rutas.RECORDATORIOS,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.IntType })
        ) { entry ->
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            val id = entry.arguments?.getInt(Rutas.ARG_ID) ?: -1
            RecordatoriosScreen(
                mascota = state.mascotaPorId(id),
                citas = state.recordatoriosDe(id),
                errores = state.erroresRecordatorio,
                onAgregar = { fecha, motivo, descripcion, onGuardado ->
                    vm.agregarRecordatorio(id, fecha, motivo, descripcion, onGuardado)
                },
                onEliminar = vm::eliminarRecordatorio,
                onLimpiarErrores = vm::limpiarErroresRecordatorio,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.AGENDA) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            AgendaScreen(
                state = state,
                onAbrirMascota = { id -> navController.navigate(Rutas.detalle(id)) },
                onVolver = { navController.popBackStack() }
            )
        }

        // Perfil: primero se VE; el botón "Editar perfil" abre el formulario.
        composable(Rutas.PERFIL) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            PerfilScreen(
                email = auth.email.orEmpty(),
                perfil = state.perfil,
                totalMascotas = state.mascotas.size,
                citasProximas = state.citasProximas.size,
                totalFavoritas = state.favoritas.size,
                onEditar = { navController.navigate(Rutas.PERFIL_EDITAR) },
                onCerrarSesion = authViewModel::logout,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PERFIL_EDITAR) {
            val vm = appViewModel ?: return@composable
            val state by vm.uiState.collectAsStateWithLifecycle()
            PerfilEditarScreen(
                perfil = state.perfil,
                errores = state.erroresPerfil,
                onGuardar = { nombre, telefono, cumpleanos, ciudad, fotoPath ->
                    vm.guardarPerfil(
                        nombre = nombre,
                        telefono = telefono,
                        cumpleanos = cumpleanos,
                        ciudad = ciudad,
                        fotoPath = fotoPath,
                        onGuardado = { navController.popBackStack() }
                    )
                },
                onLimpiarErrores = vm::limpiarErroresPerfil,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
