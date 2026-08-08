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
                    navController.navigate(EstacionListRoute)
                },
                onNavigateToCrearRuta = {
                    navController.navigate(CrearRutaRoute)
                },
                onNavigateToRecolectores = {
                    navController.navigate(ListaRecolectoresRoute)
                },
                onNavigateToAgentes = {
                    navController.navigate(AgenteListRoute)
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


        composable<EstacionListRoute> {
            EstacionListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCrear = {
                    navController.navigate(EstacionFormRoute)
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

        composable<AgenteListRoute> {
            AgenteListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCrear = { navController.navigate(AgenteFormRoute) }
            )
        }

        composable<AgenteFormRoute> {
            AgenteFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}