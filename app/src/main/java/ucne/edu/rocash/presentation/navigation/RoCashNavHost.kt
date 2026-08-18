package ucne.edu.rocash.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import ucne.edu.rocash.presentation.agenteVentas.form.AgenteFormScreen
import ucne.edu.rocash.presentation.agenteVentas.list.AgenteListScreen
import ucne.edu.rocash.presentation.auth.AuthScreen
import ucne.edu.rocash.presentation.auth.signup.SignUpScreen
import ucne.edu.rocash.presentation.estacion.form.EstacionFormScreen
import ucne.edu.rocash.presentation.estacion.list.EstacionListScreen
import ucne.edu.rocash.presentation.hojaRuta.crear.CrearRutaScreen
import ucne.edu.rocash.presentation.hojaRuta.cuadre.CuadreScreen
import ucne.edu.rocash.presentation.hojaRuta.detalle.DetalleRutaScreen
import ucne.edu.rocash.presentation.hojaRuta.historial.HistorialRutasScreen
import ucne.edu.rocash.presentation.home.HomeScreen
import ucne.edu.rocash.presentation.profile.ProfileScreen
import ucne.edu.rocash.presentation.recolector.form.FormRecolectorScreen
import ucne.edu.rocash.presentation.recolector.list.ListRecolectorScreen

@Composable
fun RoCashNavHost() {
    val navController = rememberNavController()
    val usuarioActual = FirebaseAuth.getInstance().currentUser
    val rutaInicial = if (usuarioActual != null) HomeRecolectorRoute else AuthRoute

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val menuDisponible = !currentRoute.contains("AuthRoute") && !currentRoute.contains("SignUpRoute")

    RoCashDrawer(
        drawerState = drawerState,
        gesturesEnabled = menuDisponible,
        currentRoute = currentRoute,
        onNavigateToHome = {
            scope.launch { drawerState.close() }
            navController.navigate(HomeRecolectorRoute) {
                popUpTo(HomeRecolectorRoute) { inclusive = true }
            }
        },
        onNavigateToRecolectores = {
            scope.launch { drawerState.close() }
            navController.navigate(ListaRecolectoresRoute)
        },
        onNavigateToAgentes = {
            scope.launch { drawerState.close() }
            navController.navigate(AgenteListRoute)
        },
        onNavigateToEstaciones = {
            scope.launch { drawerState.close() }
            navController.navigate(EstacionListRoute)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = rutaInicial
        ) {

            composable<AuthRoute> {
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate(HomeRecolectorRoute) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = { navController.navigate(SignUpRoute) }
                )
            }

            composable<SignUpRoute> {
                SignUpScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate(HomeRecolectorRoute) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<ProfileRoute> {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSignOutSuccess = {
                        navController.navigate(AuthRoute) { popUpTo(0) }
                    }
                )
            }

            composable<HomeRecolectorRoute> {
                HomeScreen(
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    onNavigateToCrearRuta = { navController.navigate(CrearRutaRoute) },
                    onNavigateToDetalleRuta = { rutaId ->
                        navController.navigate(DetalleRutaRoute(rutaId))
                    },
                    onNavigateToHistorial = { navController.navigate(HistorialRutaRoute) },
                    onNavigateToRecolectores = { navController.navigate(ListaRecolectoresRoute) },
                    onNavigateToAgentes = { navController.navigate(AgenteListRoute) },
                    onNavigateToEstaciones = { navController.navigate(EstacionListRoute) },
                    onNavigateToProfile = { navController.navigate(ProfileRoute) }
                )
            }

            composable<CrearRutaRoute> {
                CrearRutaScreen(
                    onRutaCreada = { rutaId ->
                        navController.navigate(DetalleRutaRoute(rutaId)) {
                            popUpTo(CrearRutaRoute) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<DetalleRutaRoute> { backStackEntry ->
                val argumentos = backStackEntry.toRoute<DetalleRutaRoute>()

                DetalleRutaScreen(
                    rutaId = argumentos.rutaId,
                    onNavigateToCuadre = { rutaId, estacionId, agenteId, nombre ->
                        navController.navigate(
                            CuadreEstacionRoute(
                                hojaRutaId = rutaId,
                                estacionId = estacionId,
                                agenteId = agenteId,
                                nombreEstacion = nombre
                            )
                        )
                    },
                    onRutaCerrada = {
                        navController.navigate(HomeRecolectorRoute) {
                            popUpTo(HomeRecolectorRoute) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<CuadreEstacionRoute> { backStackEntry ->
                val argumentos = backStackEntry.toRoute<CuadreEstacionRoute>()

                CuadreScreen(
                    hojaRutaId = argumentos.hojaRutaId,
                    estacionId = argumentos.estacionId,
                    agenteId = argumentos.agenteId,
                    nombreEstacion = argumentos.nombreEstacion,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<HistorialRutaRoute> {
                HistorialRutasScreen(
                    onNavigateToDetalleRuta = { rutaId ->
                        navController.navigate(DetalleRutaRoute(rutaId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<EstacionListRoute> {
                EstacionListScreen(
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    onNavigateToCrear = { navController.navigate(EstacionFormRoute()) },
                    onNavigateToEditar = { id -> navController.navigate(EstacionFormRoute(estacionId = id)) }
                )
            }

            composable<EstacionFormRoute> {
                EstacionFormScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable<ListaRecolectoresRoute> {
                ListRecolectorScreen(
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    onNavigateToCrear = { navController.navigate(FormRecolectorRoute()) },
                    onNavigateToEditar = { id -> navController.navigate(FormRecolectorRoute(recolectorId = id)) }
                )
            }

            composable<FormRecolectorRoute> { backStackEntry ->
                val argumentos = backStackEntry.toRoute<FormRecolectorRoute>()

                FormRecolectorScreen(
                    recolectorId = argumentos.recolectorId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<AgenteListRoute> {
                AgenteListScreen(
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    onNavigateToCrear = { navController.navigate(AgenteFormRoute()) },
                    onNavigateToEditar = { id -> navController.navigate(AgenteFormRoute(agenteId = id)) }
                )
            }

            composable<AgenteFormRoute> {
                AgenteFormScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}