package ucne.edu.rocash.presentation.core

/**
 * Snapshot inmutable y YA RESUELTO de una pantalla.
 *
 * Regla del proyecto: un UiState no calcula, no acumula, no valida y no decide.
 * Todo valor que la UI necesite mostrar debe llegar aquí como un campo listo
 * para pintarse. Si algo tiene que calcularse, se calcula en el dominio (si es
 * una regla de negocio) o en el reducer del ViewModel (si es una decisión de
 * presentación) y se deposita como campo.
 *
 * Los avisos de una sola vez (mensajes de snackbar, navegación tras guardar)
 * viajan como banderas dentro del estado —`saved`, `deleted`, `errorMessage`,
 * `navigateToX`— y la pantalla las consume con `LaunchedEffect`, apagándolas
 * después con su evento correspondiente. Es el patrón del Survival Guide y se
 * mantiene por consistencia con el resto del curso.
 */
interface UiState

/**
 * Intención que entra al ViewModel: acción del usuario o del ciclo de vida.
 * Es la única puerta de entrada; la UI nunca llama métodos sueltos del ViewModel.
 */
interface UiEvent
