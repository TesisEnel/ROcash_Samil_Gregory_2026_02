package ucne.edu.rocash.presentation.auth

import ucne.edu.rocash.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.rocash.ui.theme.coloresAccion

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    val context = LocalContext.current

    AuthBody(
        state = state,
        onEvent = { event ->
            if (event is AuthUiEvent.SignInWithGoogle) {
                viewModel.onEvent(AuthUiEvent.SignInWithGoogle(context))
            } else {
                viewModel.onEvent(event)
            }
        },
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun AuthBody(
    state: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_rocash),
                    contentDescription = "RoCash",
                    modifier = Modifier
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Bienvenido", style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onEvent(AuthUiEvent.EmailChanged(it)) },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_email"),
                    isError = state.emailError != null,
                    supportingText = state.emailError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(AuthUiEvent.PasswordChanged(it)) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_password"),
                    isError = state.passwordError != null,
                    supportingText = state.passwordError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    colors = coloresAccion(),
                    onClick = { onEvent(AuthUiEvent.SignInWithEmail) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_login_email"),
                    enabled = !state.isLoading
                ) {
                    // El spinner ahora vive de forma limpia dentro del botón
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Iniciar Sesión")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = " o ",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { onEvent(AuthUiEvent.SignInWithGoogle(context)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_login_google"),
                    enabled = !state.isLoading
                ) {
                    Text("Continuar con Google")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿No tienes cuenta? Regístrate aquí")
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("auth_error_message")
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthBodyPreview() {
    MaterialTheme {
        AuthBody(
            state = AuthUiState(),
            onEvent = {},
            onNavigateToSignUp = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthBodyErrorPreview() {
    MaterialTheme {
        AuthBody(
            state = AuthUiState(
                email = "correo_invalido",
                emailError = "El formato del correo es inválido",
                errorMessage = "Correo o contraseña incorrectos"
            ),
            onEvent = {},
            onNavigateToSignUp = {}
        )
    }
}