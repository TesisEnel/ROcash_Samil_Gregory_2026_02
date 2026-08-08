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
import ucne.edu.rocash.presentation.estacion.form.CrearEstacionScreen
import ucne.edu.rocash.presentation.estacion.list.ListaEstacionesScreen
import ucne.edu.rocash.presentation.recolector.form.FormRecolectorScreen
import ucne.edu.rocash.presentation.recolector.list.ListRecolectorScreen
import ucne.edu.rocash.presentation.ruta.CrearRutaScreen

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
                },
                onNavigateToCrearEstacion = {
                    navController.navigate(ListaEstacionesRoute)
                },
                onNavigateToCrearRuta = {
                    navController.navigate(CrearRutaRoute)
                },
                onNavigateToRecolectores = {
                    navController.navigate(ListaRecolectoresRoute)
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
            // TODO: Pantalla de Cierre
        }


        composable<ListaEstacionesRoute> {
            ListaEstacionesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCrear = {
                    navController.navigate(CrearEstacionRoute)
                }
            )
        }

        composable<CrearEstacionRoute> {
            CrearEstacionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<CrearRutaRoute> {
            CrearRutaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<ListaRecolectoresRoute> {
            ListRecolectorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCrear = {
                    navController.navigate(FormRecolectorRoute)
                }
            )
        }

        composable<FormRecolectorRoute> {
            FormRecolectorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}