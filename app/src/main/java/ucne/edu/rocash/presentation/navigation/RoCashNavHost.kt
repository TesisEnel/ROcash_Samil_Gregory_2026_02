package ucne.edu.rocash.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import ucne.edu.rocash.presentation.auth.AuthScreen // Ajusta este import a donde esté tu pantalla
import ucne.edu.rocash.presentation.home.HomeScreen

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
                onEstacionClick = { estacionId, agenteId, nombreEstacion ->
                    navController.navigate(
                        DetalleEstacionRoute(
                            estacionId = estacionId,
                            agenteId = agenteId,
                            nombreEstacion = nombreEstacion
                        )
                    )
                }
            )
        }

        composable<DetalleEstacionRoute> { backStackEntry ->
            val argumentos = backStackEntry.toRoute<DetalleEstacionRoute>()
        }

        composable<HojaRutaCierreRoute> {
        }
    }
}