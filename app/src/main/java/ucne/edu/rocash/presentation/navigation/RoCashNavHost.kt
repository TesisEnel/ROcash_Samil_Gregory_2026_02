package ucne.edu.rocash.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import ucne.edu.rocash.presentation.auth.AuthScreen
import ucne.edu.rocash.presentation.home.HomeScreen
import ucne.edu.rocash.presentation.detalle.DetalleEstacionScreen
import ucne.edu.rocash.presentation.estacion.CrearEstacionScreen

@Composable
fun RoCashNavHost() {
    val navController = rememberNavController()

    val usuarioActual = FirebaseAuth.getInstance().currentUser
    val rutaInicial = if (usuarioActual != null) HomeRecolectorRoute else HomeRecolectorRoute

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
                onEstacionClick = { estacionId, agenteId, nombreEstacion ->
                    navController.navigate(
                        DetalleEstacionRoute(
                            estacionId = estacionId,
                            agenteId = agenteId,
                            nombreEstacion = nombreEstacion
                        )
                    )
                },
                onNavigateToCrearEstacion = {
                    navController.navigate(CrearEstacionRoute)
                },
                onNavigateToCrearRuta = {
                    navController.navigate(CrearRutaRoute)
                }
            )
        }

        composable<DetalleEstacionRoute> { backStackEntry ->
            val argumentos = backStackEntry.toRoute<DetalleEstacionRoute>()

            DetalleEstacionScreen(
                estacionId = argumentos.estacionId,
                agenteId = argumentos.agenteId,
                nombreEstacion = argumentos.nombreEstacion,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<HojaRutaCierreRoute> {
        }

        composable<CrearEstacionRoute> {
            CrearEstacionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}