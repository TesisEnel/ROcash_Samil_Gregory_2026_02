package ucne.edu.rocash.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import ucne.edu.rocash.presentation.agenteVentas.form.AgenteFormScreen
import ucne.edu.rocash.presentation.agenteVentas.list.AgenteListScreen
import ucne.edu.rocash.presentation.auth.AuthScreen
import ucne.edu.rocash.presentation.home.HomeScreen
import ucne.edu.rocash.presentation.detalle.DetalleEstacionScreen
import ucne.edu.rocash.presentation.estacion.form.EstacionFormScreen
import ucne.edu.rocash.presentation.estacion.list.EstacionListScreen
import ucne.edu.rocash.presentation.recolector.form.FormRecolectorScreen
import ucne.edu.rocash.presentation.recolector.list.ListRecolectorScreen
import ucne.edu.rocash.presentation.ruta.CrearRutaScreen
import ucne.edu.rocash.presentation.ruta.historial.HistorialRutasScreen

@Composable
fun RoCashNavHost() {
    val navController = rememberNavController()
    val usuarioActual = FirebaseAuth.getInstance().currentUser
    val rutaInicial = if (usuarioActual != null) HomeRecolectorRoute else AuthRoute

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
                }
            )
        }

        composable<HomeRecolectorRoute> {
            HomeScreen(
                onNavigateToCrearRuta = { navController.navigate(CrearRutaRoute) },
                onNavigateToHistorial = { navController.navigate(HistorialRutaRoute) },
                onNavigateToRecolectores = { navController.navigate(ListaRecolectoresRoute) },
                onNavigateToAgentes = { navController.navigate(AgenteListRoute) },
                onNavigateToEstaciones = { navController.navigate(EstacionListRoute) },
                onNavigateToDetalleEstacion = { rutaId, estacionId, agenteId, nombre ->
                    navController.navigate(
                        DetalleEstacionRoute(
                            hojaRutaId = rutaId,
                            estacionId = estacionId,
                            agenteId = agenteId,
                            nombreEstacion = nombre
                        )
                    )
                }
            )
        }

        composable<DetalleEstacionRoute> { backStackEntry ->
            val argumentos = backStackEntry.toRoute<DetalleEstacionRoute>()

            DetalleEstacionScreen(
                hojaRutaId = argumentos.hojaRutaId,
                estacionId = argumentos.estacionId,
                agenteId = argumentos.agenteId,
                nombreEstacion = argumentos.nombreEstacion,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<HojaRutaCierreRoute> {
            // TODO: Pantalla de Cierre
        }

        composable<EstacionListRoute> {
            EstacionListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCrear = { navController.navigate(EstacionFormRoute()) },
                onNavigateToEditar = { id ->
                    navController.navigate(EstacionFormRoute(estacionId = id))
                }
            )
        }

        composable<EstacionFormRoute> {
            EstacionFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<CrearRutaRoute> {
            CrearRutaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<ListaRecolectoresRoute> {
            ListRecolectorScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCrear = { navController.navigate(FormRecolectorRoute()) },
                onNavigateToEditar = { id ->
                    navController.navigate(FormRecolectorRoute(recolectorId = id))
                }
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCrear = { navController.navigate(AgenteFormRoute()) },
                onNavigateToEditar = { id ->
                    navController.navigate(AgenteFormRoute(agenteId = id))
                }
            )
        }

        composable<AgenteFormRoute> {
            AgenteFormScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HistorialRutaRoute> {
            HistorialRutasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}